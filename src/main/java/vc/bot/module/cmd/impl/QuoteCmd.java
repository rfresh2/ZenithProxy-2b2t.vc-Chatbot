package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.ChatsApi;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class QuoteCmd extends Cmd {
    private final ChatsApi chatsApi = new ChatsApi(apiClient().setReadTimeout(Duration.ofSeconds(60)));

    @Override
    public List<String> cmdAliases() {
        return List.of("quote");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetName = targetName(sender, args);
        var response = chatsApi.chats(null, targetName, null, null, null, "desc", 100, null);
        if (response == null || response.getChats() == null) {
            return null;
        }
        if (response.getTotal() == 0) {
            return "%s has not chatted".formatted(targetName);
        }
        var chat = response.getChats().get(ThreadLocalRandom.current().nextInt(0, response.getChats().size()));
        if (response.getPageCount() > 1) {
            var randomPage = ThreadLocalRandom.current().nextInt(1, response.getPageCount()+1);
            var response2 = chatsApi.chats(null, targetName, null, null, null, "desc", 100, randomPage);
            if (response2 != null && response2.getChats() != null && !response2.getChats().isEmpty()) {
                chat = response2.getChats().get(ThreadLocalRandom.current().nextInt(0, response2.getChats().size()));
            }
        }
        return "[%s] <%s> %s".formatted(
            defaultDateFormatter().format(chat.getTime()),
            targetName,
            chat.getChat());
    }
}
