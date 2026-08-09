package vc.bot.module;

import com.github.rfresh2.EventConsumer;
import com.zenith.Proxy;
import com.zenith.event.chat.PublicChatEvent;
import com.zenith.event.chat.WhisperChatEvent;
import com.zenith.module.api.Module;
import com.zenith.util.ChatUtil;
import com.zenith.util.Wait;
import com.zenith.util.timer.Timer;
import com.zenith.util.timer.Timers;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import org.jspecify.annotations.Nullable;
import vc.bot.cmd.Cmd;
import vc.bot.cmd.CmdRegistry;
import vc.bot.util.RandomString;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static com.github.rfresh2.EventConsumer.of;
import static com.zenith.Globals.CACHE;
import static vc.bot.VcBotPlugin.PLUGIN_CONFIG;

public class VcChatbotModule extends Module {
    private final ExecutorService cmdExecutor = Executors.newSingleThreadExecutor();
    private final Timer chatCooldownTimer = Timers.tickTimer();

    @Override
    public boolean enabledSetting() {
        return PLUGIN_CONFIG.enabled;
    }

    @Override
    public List<EventConsumer<?>> registerEvents() {
        return List.of(
            of(PublicChatEvent.class, this::onPublicChat),
            of(WhisperChatEvent.class, this::onWhisper)
        );
    }

    interface CommandResponder {
        @Nullable MinecraftPacket buildResponsePacket(String response);
    }

    void onPublicChat(PublicChatEvent event) {
        if (!PLUGIN_CONFIG.publicChat) return;
        handleIncoming(event.message(), event.sender(), ServerboundChatPacket::new);
    }

    private void onWhisper(WhisperChatEvent event) {
        if (!PLUGIN_CONFIG.whispers) return;
        if (event.outgoing()) return;
        handleIncoming(event.message(), event.sender(), r -> ChatUtil.getWhisperChatPacket(event.sender().getName(), r));
    }

    private void handleIncoming(String input, PlayerListEntry sender, CommandResponder responder) {
        input = input.trim();
        if (!PLUGIN_CONFIG.prefix.contains(">")) {
            // chop off any greentext prefix
            while (input.startsWith(">") && input.toLowerCase().contains(PLUGIN_CONFIG.prefix)) {
                input = input.substring(1).trim();
            }
        }
        if (!input.toLowerCase().startsWith(PLUGIN_CONFIG.prefix)) {
            return;
        }
        var msg = input.substring(PLUGIN_CONFIG.prefix.length());
        if (msg.isBlank() || msg.charAt(0) == ' ') return;
        var msgSplit = Stream.of(msg.split(" "))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .toList();
        if (msgSplit.isEmpty()) {
            return;
        }
        var cmdStr = msgSplit.getFirst().toLowerCase();
        var cmd = CmdRegistry.INSTANCE.cmds().get(cmdStr);
        if (cmd == null) {
            return;
        }
        List<String> argsList = msgSplit.size() == 1 ? Collections.emptyList() : msgSplit.subList(1, msgSplit.size());
        // we are already on an executor thread, but we would otherwise block other event consumers
        cmdExecutor.execute(() -> execute(msg, cmd, sender, argsList, responder));
    }

    private void execute(String msg, Cmd cmd, PlayerListEntry sender, List<String> argsList, CommandResponder responder) {
        try {
            // we could potentially have a backlog of commands being executed after dc
            // so just sanity check we and the sender are still online
            if (!Proxy.getInstance().isConnected()) {
                info("Not connected, ignoring cmd {}", msg);
                return;
            }
            if (CACHE.getTabListCache().get(sender.getProfileId()).isEmpty()) {
                info("Sender is offline, ignoring cmd {}", msg);
                return;
            }
            if (!isEnabled()) return;
            var result = cmd.handle(sender, argsList); // api call, will block for substantial time
            if (result == null) {
                info("empty response for cmd {}", msg);
                return;
            }
            Wait.waitUntil(() -> chatCooldownTimer.tick(PLUGIN_CONFIG.chatCooldownTicks), (PLUGIN_CONFIG.chatCooldownTicks / 20) + 1);
            if (!isEnabled()) return;
            if (PLUGIN_CONFIG.responsePrefixEnabled) {
                result = PLUGIN_CONFIG.responsePrefix + result;
            }
            if (PLUGIN_CONFIG.appendRandomSuffix) {
                result += " [" + RandomString.generate(6) + "]";
            }
            var responsePacket = responder.buildResponsePacket(result);
            if (responsePacket != null) {
                info("{}", result);
                sendClientPacketAsync(responsePacket);
            }
        } catch (IllegalArgumentException e) {
            debug(msg, e);
        } catch (Exception e) {
            error(msg, e);
        }
    }
}
