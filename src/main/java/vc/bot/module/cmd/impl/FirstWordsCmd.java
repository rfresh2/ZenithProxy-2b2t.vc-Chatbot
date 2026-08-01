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
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return null;
        }
        try {
            var response = chatsApi.chats(null, targetProfile.get().name(), targetProfile.get().uuid(), null, null, "asc", 1, 0);
            if (response == null) {
                return null;
            }
            if (response.getChats().isEmpty()) {
                return "No data found for %s".formatted(targetProfile.get().name());
            }
            return "[%s] <%s> %s".formatted(
                defaultDateFormatter().format(response.getChats().getFirst().getTime()),
                targetProfile.get().name(),
                response.getChats().getFirst().getChat()
            );
        } catch (ApiException e) {
            if (e.getCode() == 204) {
                return "No data found for %s".formatted(targetProfile.get().name());
            }
            error("API error", e);
            return null;
        }
    }
}
