package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.openapi.handler.ApiException;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;

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
            var response = ApiClients.INSTANCE.chatsApi().chats(null, targetProfile.get().name(), targetProfile.get().uuid(), null, null, "asc", 1, 0);
            if (response == null) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            if (response.getChats().isEmpty()) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            var chat = response.getChats().getFirst();
            return "[%s] <%s> %s".formatted(
                dateTimeFormatter().format(chat.getTime()),
                chat.getPlayerName(),
                chat.getChat()
            );
        } catch (ApiException e) {
            if (e.getCode() == 204) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            error("API error", e);
            return null;
        }
    }
}
