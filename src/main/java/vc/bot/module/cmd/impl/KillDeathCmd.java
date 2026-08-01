package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.StatsApi;

import java.util.List;

public class KillDeathCmd extends Cmd {
    private final StatsApi statsApi = new StatsApi(apiClient());

    @Override
    public List<String> cmdAliases() {
        return List.of("kd", "kills", "deaths");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetName = targetName(sender, args);
        var response = statsApi.playerStats(null, targetName);
        if (response == null) {
            return null;
        }
        return "%s has: %s kills, %s deaths, %.2f K/D".formatted(
            targetName,
            response.getKillCount(),
            response.getDeathCount(),
            response.getDeathCount() == 0
                ? response.getKillCount() == 0
                    ? "0"
                    : "1.0"
                : response.getKillCount() / (double) response.getDeathCount()
        );
    }
}
