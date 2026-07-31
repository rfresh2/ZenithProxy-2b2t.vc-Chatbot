package vc.bot.module.cmd.impl;

import lombok.SneakyThrows;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.SeenApi;

import java.util.List;

public class JoinDateCmd extends Cmd {
    private final SeenApi seenApi = new SeenApi(apiClient());

    @Override
    public List<String> cmdAliases() {
        return List.of("joindate", "join", "jd");
    }

    @SneakyThrows
    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) {
        var targetName = sender.getName();
        if (!args.isEmpty()) {
            targetName = args.getFirst();
        }
        var response = seenApi.seen(null, targetName);
        var firstSeen = response.getFirstSeen();
        if (firstSeen == null) {
            return null;
        }
        var dateFmt = defaultDateFormatter()
            .format(firstSeen);
        return "%s was first seen at %s".formatted(sender.getName(), dateFmt);
    }
}
