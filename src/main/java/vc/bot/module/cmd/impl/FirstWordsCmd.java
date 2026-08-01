package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.ChatsApi;

import java.time.Duration;
import java.util.List;

public class FirstWordsCmd extends Cmd {
    private final ChatsApi chatsApi = new ChatsApi(apiClient().setReadTimeout(Duration.ofSeconds(60)));

    @Override
    public List<String> cmdAliases() {
        return List.of("firstwords");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetName = targetName(sender, args);
        var response = chatsApi.chats(null, targetName, null, null, null, "asc", 1, 0);
        if (response == null || response.getChats() == null || response.getChats().isEmpty()) {
            return null;
        }
        return "[%s] <%s> %s".formatted(
            defaultDateFormatter().format(response.getChats().getFirst().getTime()),
            targetName,
            response.getChats().getFirst().getChat()
        );
    }
}
