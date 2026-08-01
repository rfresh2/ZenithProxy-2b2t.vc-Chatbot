package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.SeenApi;

import java.util.List;

public class LastSeenCmd extends Cmd {
    private final SeenApi seenApi = new SeenApi(apiClient());

    @Override
    public List<String> cmdAliases() {
        return List.of("seen", "lastseen");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetName = targetName(sender, args);
        var response = seenApi.seen(null, targetName);
        if (response == null || response.getLastSeen() == null) {
            return null;
        }
        return "%s was last seen on %s".formatted(
            targetName,
            defaultDateFormatter().format(response.getLastSeen())
        );
    }
}
