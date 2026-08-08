package vc.bot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.api.ApiClients;
import vc.bot.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;

import java.util.List;

public class PlaytimeCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("playtime", "pt");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return null;
        }
        try {
            var response = ApiClients.INSTANCE.playtimeApi().playtime(targetProfile.get().uuid(), targetProfile.get().name());
            if (response == null) {
                return null;
            }
            if (response.getPlaytimeSeconds() == null) {
                return "No data found for %s".formatted(targetProfile.get().name());
            }
            var durationStr = formatDuration(response.getPlaytimeSeconds());
            return "%s has played for %s".formatted(
                targetProfile.get().name(),
                durationStr
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
