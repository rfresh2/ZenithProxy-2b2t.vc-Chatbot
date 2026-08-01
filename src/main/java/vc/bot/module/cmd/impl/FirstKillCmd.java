package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.DeathsApi;

import java.util.List;

public class FirstKillCmd extends Cmd {
    private final DeathsApi deathsApi = new DeathsApi(apiClient());

    @Override
    public List<String> cmdAliases() {
        return List.of("firstkill", "fk");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetName = targetName(sender, args);
        var response = deathsApi.kills(null, targetName, null, null, "asc", 1, null);
        if (response == null || response.getKills() == null || response.getKills().isEmpty()) {
            return null;
        }
        return "[%s] %s".formatted(
            defaultDateFormatter().format(response.getKills().getFirst().getTime()),
            response.getKills().getFirst().getDeathMessage()
        );
    }
}
