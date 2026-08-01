package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;

import java.util.List;

public class ReportCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("report");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetName = targetName(sender, args);
        return "%s has been reported to the admins!".formatted(targetName);
    }
}
