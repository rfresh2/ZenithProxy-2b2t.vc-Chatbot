package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class QuoteCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("quote");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return noPlayerFoundMsg(targetName(sender, args));
        }
        try {
            var response = ApiClients.INSTANCE.chatsApi().chats(null, targetProfile.get().name(), targetProfile.get().uuid(), null, null, "desc", 100, null);
            if (response == null) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            if (response.getChats().isEmpty()) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            var chat = response.getChats().get(ThreadLocalRandom.current().nextInt(0, response.getChats().size()));
            if (response.getPageCount() > 1) {
                var randomPage = ThreadLocalRandom.current().nextInt(1, response.getPageCount() + 1);
                if (randomPage > 1) {
                    try {
                        var response2 = ApiClients.INSTANCE.chatsApi().chats(null, targetProfile.get().name(), targetProfile.get().uuid(), null, null, "desc", 100, randomPage);
                        if (response2 != null && !response2.getChats().isEmpty()) {
                            chat = response2.getChats().get(ThreadLocalRandom.current().nextInt(0, response2.getChats().size()));
                        }
                    } catch (ApiException e) {
                        if (e.getCode() != 204) {
                            error("API error", e);
                        }
                    }
                }
            }
            return "[%s] <%s> %s".formatted(
                dateTimeFormatter().format(chat.getTime()),
                chat.getPlayerName(),
                chat.getChat());
        } catch (ApiException e) {
            if (e.getCode() == 204) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            error("API error", e);
            return null;
        }
    }
}
