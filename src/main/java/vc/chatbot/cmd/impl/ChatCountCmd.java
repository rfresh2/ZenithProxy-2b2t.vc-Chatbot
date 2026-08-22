package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.util.List;

public class ChatCountCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("chats", "messages", "chatcount", "msgs");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return noPlayerFoundMsg(targetName(sender, args));
        }
        try {
            var response = ApiClients.INSTANCE.chatsApi().chatsWithHttpInfo(null, targetProfile.get().name(), targetProfile.get().uuid(), null, null, "desc", 1, null);
            if (response.getStatusCode() == 204 || response.getData() == null || response.getData().getTotal() == null) {
                return "%s has never chatted".formatted(targetProfile.get().name());
            }
            return "%s has sent %s chats".formatted(targetProfile.get().name(), response.getData().getTotal());
        } catch (ApiException e) {
            error("API error", e);
            return null;
        }
    }
}
