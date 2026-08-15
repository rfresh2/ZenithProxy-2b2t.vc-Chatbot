package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.util.List;

public class FirstWordsCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("firstwords");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return noPlayerFoundMsg(targetName(sender, args));
        }
        try {
            var response = ApiClients.INSTANCE.chatsApi().chatsWithHttpInfo(null, targetProfile.get().name(), targetProfile.get().uuid(), null, null, "asc", 1, 0);
            if (response.getStatusCode() == 204 || response.getData() == null || response.getData().getChats().isEmpty()) {
                return "%s has never said anything";
            }
            var chat = response.getData().getChats().getFirst();
            return "[%s] <%s> %s".formatted(
                dateTimeFormatter().format(chat.getTime()),
                chat.getPlayerName(),
                chat.getChat()
            );
        } catch (ApiException e) {
            error("API error", e);
            return null;
        }
    }
}
