package vc.bot.module;

import com.github.rfresh2.EventConsumer;
import com.zenith.event.chat.PublicChatEvent;
import com.zenith.module.api.Module;
import com.zenith.util.ChatUtil;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import vc.bot.module.cmd.CmdRegistry;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.github.rfresh2.EventConsumer.of;
import static vc.bot.VcBotPlugin.PLUGIN_CONFIG;

public class VcChatbotModule extends Module {
    @Override
    public boolean enabledSetting() {
        return PLUGIN_CONFIG.enabled;
    }

    @Override
    public List<EventConsumer<?>> registerEvents() {
        return List.of(
            of(PublicChatEvent.class, this::onPublicChat)
        );
    }

    void onPublicChat(PublicChatEvent event) {
        var input = event.message().trim();
        if (!input.startsWith(PLUGIN_CONFIG.prefix)) {
            return;
        }
        var msg = input.substring(PLUGIN_CONFIG.prefix.length());
        var msgSplit = Stream.of(msg.split(" "))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .toList();
        if (msgSplit.isEmpty()) {
            return;
        }
        var cmd = msgSplit.getFirst().toLowerCase();
        var cmdInstance = CmdRegistry.INSTANCE.cmds().get(cmd);
        if (cmdInstance == null) {
            return;
        }
        List<String> argsList = msgSplit.size() == 1 ? Collections.emptyList() : msgSplit.subList(1, msgSplit.size());
        try {
            var result = cmdInstance.handle(event.sender(), argsList);
            if (result == null) {
                return;
            }
            sendClientPacketAsync(new ServerboundChatPacket(ChatUtil.sanitizeChatMessage(result)));
        } catch (IllegalArgumentException e) {
            debug(msg, e);
        } catch (Exception e) {
            error(msg, e);
        }
    }
}
