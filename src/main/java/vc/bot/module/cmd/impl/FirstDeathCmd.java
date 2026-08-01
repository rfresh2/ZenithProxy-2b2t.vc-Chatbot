package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.DeathsApi;

import java.util.List;

public class FirstDeathCmd extends Cmd {
    private final DeathsApi deathsApi = new DeathsApi(apiClient());

    @Override
    public List<String> cmdAliases() {
        return List.of("firstdeath", "fd");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetName = targetName(sender, args);
        var response = deathsApi.deaths(null, targetName, null, null, "asc", 1, null);
        if (response == null || response.getDeaths() == null || response.getDeaths().isEmpty()) {
            return null;
        }
        return "[%s] %s".formatted(
            defaultDateFormatter().format(response.getDeaths().getFirst().getTime()),
            response.getDeaths().getFirst().getDeathMessage()
        );
    }
}
