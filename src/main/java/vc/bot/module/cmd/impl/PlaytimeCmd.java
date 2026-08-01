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
        var targetName = targetName(sender, args);
        var response = playtimeApi.playtime(null, targetName);
        if (response == null || response.getPlaytimeSeconds() == null) {
            return null;
        }
        var durationStr = MathHelper.formatDuration(Duration.ofSeconds(response.getPlaytimeSeconds()));
        return "%s has played for %s".formatted(
            targetName,
            durationStr
        );
    }
}
