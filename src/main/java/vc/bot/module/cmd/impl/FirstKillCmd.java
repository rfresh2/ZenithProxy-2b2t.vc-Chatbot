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
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return null;
        }
        try {
            var response = deathsApi.kills(targetProfile.get().uuid(), targetProfile.get().name(), null, null, "asc", 1, null);
            if (response == null) {
                return null;
            }
            if (response.getKills().isEmpty()) {
                return "No data found for %s".formatted(targetProfile.get().name());
            }
            return "[%s] %s".formatted(
                defaultDateFormatter().format(response.getKills().getFirst().getTime()),
                response.getKills().getFirst().getDeathMessage()
            );
        } catch (ApiException e) {
            if (e.getCode() == 204) {
                return "No data found for %s".formatted(targetProfile.get().name());
            }
            error("API error", e);
            return null;
        }
    }
}
