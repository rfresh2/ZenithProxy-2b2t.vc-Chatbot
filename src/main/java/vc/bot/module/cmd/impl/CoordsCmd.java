package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CoordsCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("coords", "locate");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return null;
        }
        return "%s's coords are %s %s".formatted(
            targetProfile.get().name(),
            ThreadLocalRandom.current().nextInt(-30_000_000, 30_000_000),
            ThreadLocalRandom.current().nextInt(-30_000_000, 30_000_000)
        );
    }
}
