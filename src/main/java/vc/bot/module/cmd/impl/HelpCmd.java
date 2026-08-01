package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;

import java.util.List;

public class HelpCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("help");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        return "Commands: !seen !pt !jd !quote !lastseen !kd !nolife !clout !report - and many more";
    }
}
