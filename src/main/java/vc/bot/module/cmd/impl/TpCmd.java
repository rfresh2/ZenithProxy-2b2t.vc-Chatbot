package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TpCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("tp");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetName = targetName(sender, args);
        return "%s has been teleported to %s %s".formatted(
            targetName,
            ThreadLocalRandom.current().nextInt(-30_000_000, 30_000_000),
            ThreadLocalRandom.current().nextInt(-30_000_000, 30_000_000)
        );
    }
}
