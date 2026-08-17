package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public class TimeCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("time");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        // we could use local cached time if we're on 2b2t and in the ow
        // but it will make the plugin incompat with future mc versions
        // as the cached time classes have changed
        try {
            var response = ApiClients.INSTANCE.timeApi().getWorldTime();
            if (response == null || response.getWorldTime() == null || response.getLastUpdated() == null) {
                return noDataFoundMsg();
            }
            var timeDiffDuration = Duration.between(response.getLastUpdated(), OffsetDateTime.now());
            var timeDiffTicks = timeDiffDuration.toMillis() / 50L;
            var extrapolatedTime = response.getWorldTime() + timeDiffTicks;
            var curTickTime = extrapolatedTime % 24000L;
            // tick time is between: 0 -> 24000
            // Daytime starts at 6:00 (0 ticks) and night begins at 18:00 (12000 ticks)
            var h = ((curTickTime / 1000L) + 6) % 24;
            var min = curTickTime % 1000L;
            var dayNight = h >= 6 && h < 18 ? "day" : "night";
            return "it is currently %s - %s:%s (%s ticks)".formatted(
                dayNight,
                h,
                min,
                curTickTime
            );
        } catch (ApiException e) {
            error("API error", e);
            return null;
        }
    }
}
