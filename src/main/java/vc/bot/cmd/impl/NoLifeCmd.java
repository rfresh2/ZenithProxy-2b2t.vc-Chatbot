package vc.bot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.api.ApiClients;
import vc.bot.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public class NoLifeCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("nolife");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return null;
        }
        try {
            var response = ApiClients.INSTANCE.statsApi().playerStats(targetProfile.get().uuid(), targetProfile.get().name());
            if (response == null) {
                return null;
            }
            var firstSeen = response.getFirstSeen();
            var ptSeconds = response.getPlaytimeSeconds();
            long secondsSinceFirstSeen = Duration.between(firstSeen, OffsetDateTime.now()).toSeconds();
            double proportionOnline = ptSeconds / (double) secondsSinceFirstSeen;
            // todo: i think this is wrong
            Duration durationPerDay = Duration.ofSeconds((long) (Duration.ofDays(1).toSeconds() * proportionOnline));
            return "%s has wasted %.2f%% of their life since joining! Avg per day: %s".formatted(
                targetProfile.get().name(),
                proportionOnline * 100.0,
                formatDuration(durationPerDay.toSeconds())
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
