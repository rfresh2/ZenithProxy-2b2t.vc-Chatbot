package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.SeenApi;

import java.util.List;

public class FirstSeenCmd extends Cmd {
    private final SeenApi seenApi = new SeenApi(apiClient());

    @Override
    public List<String> cmdAliases() {
        return List.of("joindate", "join", "jd", "firstseen");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetName = targetName(sender, args);
        var response = seenApi.seen(null, targetName);
        var firstSeen = response.getFirstSeen();
        if (firstSeen == null) {
            return null;
        }
        var dateFmt = defaultDateFormatter()
            .format(firstSeen);
        return "%s was first seen on %s".formatted(sender.getName(), dateFmt);
    }
}
