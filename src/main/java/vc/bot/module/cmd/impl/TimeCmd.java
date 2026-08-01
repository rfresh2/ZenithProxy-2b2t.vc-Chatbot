package vc.bot.module.cmd.impl;

import com.zenith.feature.player.World;
import com.zenith.mc.dimension.DimensionRegistry;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.TimeApi;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import static com.zenith.Globals.CACHE;

public class TimeCmd extends Cmd {
    private final TimeApi timeApi = new TimeApi(apiClient());

    @Override
    public List<String> cmdAliases() {
        return List.of("time");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var worldTimeData = CACHE.getChunkCache().getWorldTimeData();
        // 0 - 24000
        // Daytime starts at 6:00 (0 ticks) and night begins at 18:00 (12000 ticks)
        long curTickTime;
        if (World.getCurrentDimension() != DimensionRegistry.OVERWORLD.get() || worldTimeData == null) {
            try {
                var response = timeApi.getWorldTime();
                if (response == null) {
                    return null;
                }
                if (response.getWorldTime() == null) {
                    return "No data found";
                }

                var worldTime = response.getWorldTime();
                var updatedTime = response.getLastUpdated();
                var timeLag = Duration.between(updatedTime, OffsetDateTime.now());
                var ticksLag = timeLag.toMillis() / 50L;
                curTickTime = (worldTime + ticksLag) % 24000L;
            } catch (ApiException e) {
                if (e.getCode() == 204) {
                    return "No data found";
                }
                error("API error", e);
                return null;
            }
        } else {
            var worldTime = worldTimeData.isTickDayTime() ? worldTimeData.getDayTime() : -worldTimeData.getDayTime();
            curTickTime = worldTime % 24000L;
        }
        var h = ((curTickTime / 1000L) + 6) % 24;
        var min = curTickTime % 1000L;
        return "the time is currently %s:%s (%s ticks)".formatted(
            h,
            min,
            curTickTime
        );
    }
}
