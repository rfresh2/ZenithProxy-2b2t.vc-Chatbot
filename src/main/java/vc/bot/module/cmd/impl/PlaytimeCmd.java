package vc.bot.module.cmd.impl;

import com.zenith.util.math.MathHelper;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.PlaytimeApi;

import java.time.Duration;
import java.util.List;

public class PlaytimeCmd extends Cmd {
    private final PlaytimeApi playtimeApi = new PlaytimeApi(apiClient());

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
            var response = playtimeApi.playtime(targetProfile.get().uuid(), targetProfile.get().name());
            if (response == null) {
                return null;
            }
            if (response.getPlaytimeSeconds() == null) {
                return "No data found for %s".formatted(targetProfile.get().name());
            }
            var durationStr = MathHelper.formatDuration(Duration.ofSeconds(response.getPlaytimeSeconds()));
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
