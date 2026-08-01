package vc.bot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.api.ApiClients;
import vc.bot.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;

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
            return null;
        }
        try {
            var response = ApiClients.INSTANCE.chatsApi().chats(null, targetProfile.get().name(), targetProfile.get().uuid(), null, null, "desc", 100, null);
            if (response == null) {
                return null;
            }
            if (response.getChats().isEmpty()) {
                return "No data found for %s".formatted(targetProfile.get().name());
            }
            var chat = response.getChats().get(ThreadLocalRandom.current().nextInt(0, response.getChats().size()));
            if (response.getPageCount() > 1) {
                var randomPage = ThreadLocalRandom.current().nextInt(1, response.getPageCount() + 1);
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
            return "[%s] <%s> %s".formatted(
                dateTimeFormatter().format(chat.getTime()),
                targetProfile.get().name(),
                chat.getChat());
        } catch (ApiException e) {
            if (e.getCode() == 204) {
                return "No data found for %s".formatted(targetProfile.get().name());
            }
            error("API error", e);
            return null;
        }
    }
}
