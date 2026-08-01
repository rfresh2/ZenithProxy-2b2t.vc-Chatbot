package vc.bot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.api.ApiClients;
import vc.bot.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;

import java.util.List;

public class FirstDeathCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("firstdeath", "fd");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return null;
        }
        try {
            var response = ApiClients.INSTANCE.deathsApi().deaths(targetProfile.get().uuid(), targetProfile.get().name(), null, null, "asc", 1, null);
            if (response == null) {
                return null;
            }
            if (response.getDeaths().isEmpty()) {
                return "No data found for %s".formatted(targetProfile.get().name());
            }
            return "[%s] %s".formatted(
                dateTimeFormatter().format(response.getDeaths().getFirst().getTime()),
                response.getDeaths().getFirst().getDeathMessage()
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
