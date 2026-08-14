package vc.chatbot.module;

import com.github.rfresh2.EventConsumer;
import com.zenith.Proxy;
import com.zenith.event.chat.PublicChatEvent;
import com.zenith.event.chat.WhisperChatEvent;
import com.zenith.event.client.ClientConfigurationEvent;
import com.zenith.event.client.ClientDisconnectEvent;
import com.zenith.event.client.ClientOnlineEvent;
import com.zenith.event.client.ClientTickEvent;
import com.zenith.module.api.Module;
import com.zenith.util.ChatUtil;
import com.zenith.util.Wait;
import com.zenith.util.math.MathHelper;
import com.zenith.util.timer.Timer;
import com.zenith.util.timer.Timers;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import org.jspecify.annotations.Nullable;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.cmd.CmdRegistry;
import vc.chatbot.util.RandomString;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static com.github.rfresh2.EventConsumer.of;
import static com.zenith.Globals.CACHE;
import static com.zenith.Globals.PLAYER_LISTS;
import static vc.chatbot.ChatbotPlugin.PLUGIN_CONFIG;

public class ChatbotModule extends Module {
    private final ExecutorService cmdExecutor = Executors.newSingleThreadExecutor();
    private final Timer chatCooldownTimer = Timers.tickTimer();
    private final Map<String, SentChat> sentChats = new ConcurrentHashMap<>();

    @Override
    public boolean enabledSetting() {
        return PLUGIN_CONFIG.enabled;
    }

    @Override
    public List<EventConsumer<?>> registerEvents() {
        return List.of(
            of(PublicChatEvent.class, this::onPublicChat),
            of(WhisperChatEvent.class, this::onWhisper),
            of(PublicChatEvent.class, this::onPublicChatMuteDetect),
            of(ClientOnlineEvent.class, e -> resetSentChats()),
            of(ClientDisconnectEvent.class, e -> resetSentChats()),
            of(ClientConfigurationEvent.Entered.class, e -> resetSentChats()),
            of(ClientTickEvent.class, e -> muteDetectExec())
        );
    }

    @Override
    public void onEnable() {
        resetSentChats();
    }

    @Override
    public void onDisable() {
        resetSentChats();
    }

    private void resetSentChats() {
        sentChats.clear();
    }

    private void onPublicChatMuteDetect(PublicChatEvent event) {
        if (!PLUGIN_CONFIG.responseRetry) return;
        var selfProfile = CACHE.getProfileCache().getProfile();
        if (selfProfile == null) {
            resetSentChats();
            return;
        }
        if (!event.sender().getProfileId().equals(selfProfile.getId())) {
            return;
        }
        sentChats.remove(event.message());
    }

    private void muteDetectExec() {
        if (!PLUGIN_CONFIG.responseRetry) {
            sentChats.clear();
            return;
        }
        for (var it = sentChats.values().iterator(); it.hasNext(); ) {
            var sentChat = it.next();
            if (sentChat.getAttempts() > 1) {
                it.remove();
                continue;
            }
            var retryTimeoutTicks = Math.max(5 * 20, PLUGIN_CONFIG.chatCooldownTicks + 20);
            var retryTimeoutSeconds = MathHelper.ceilI(retryTimeoutTicks / 20.0);
            if (sentChat.getTime().isBefore(Instant.now().minusSeconds(retryTimeoutSeconds))) {
                sentChat.setAttempts(sentChat.getAttempts() + 1);
                sentChat.setTime(Instant.now());
                String msg = sentChat.getMsg();
                if (PLUGIN_CONFIG.responseRandomSuffix) {
                    var randomStartIndex = msg.lastIndexOf('[');
                    if (randomStartIndex != -1) {
                        msg = msg.substring(0, randomStartIndex).trim();
                        msg += " [" + RandomString.generate(6) + "]";
                        sentChat = new SentChat(msg, Instant.now(), sentChat.getAttempts());
                        sentChats.put(msg, sentChat);
                        it.remove();
                    }
                }
                error("chat filtered, retrying: {}", msg);
                final var finalMsg = msg;
                final var finalSentChat = sentChat;
                cmdExecutor.execute(() -> {
                    Wait.waitUntil(() -> chatCooldownTimer.tick(PLUGIN_CONFIG.chatCooldownTicks), (PLUGIN_CONFIG.chatCooldownTicks / 20) + 1);
                    finalSentChat.setTime(Instant.now());
                    sendClientPacketAsync(new ServerboundChatPacket(finalMsg));
                });
            }
        }
    }

    @Data
    @AllArgsConstructor
    static final class SentChat {
        private final String msg;
        private Instant time;
        private int attempts;
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
        if (PLAYER_LISTS.getIgnoreList().contains(sender.getProfileId())) {
            return;
        }
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
            if (PLUGIN_CONFIG.responseRandomSuffix) {
                result += " [" + RandomString.generate(6) + "]";
            }
            var responsePacket = responder.buildResponsePacket(result);
            if (responsePacket != null) {
                info("{}", result);
                sentChats.put(result, new SentChat(result, Instant.now(), 0));
                sendClientPacketAsync(responsePacket);
            }
        } catch (IllegalArgumentException e) {
            debug(msg, e);
        } catch (Exception e) {
            error(msg, e);
        }
    }
}
