package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

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
            return noPlayerFoundMsg(targetName(sender, args));
        }
        try {
            var response = ApiClients.INSTANCE.statsApi().playerStats(targetProfile.get().uuid(), targetProfile.get().name());
            if (response == null) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            if (response.getPlaytimeSeconds() == null || response.getPlaytimeSecondsMonth() == null) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            if (response.getPlaytimeSeconds() == 0) {
                return "%s has never played".formatted(targetProfile.get().name());
            }
            var part1 = "%s has played for %s".formatted(
                targetProfile.get().name(),
                formatDuration(response.getPlaytimeSeconds())
            );
            String part2;
            if (response.getPlaytimeSeconds() == 0) {
                part2 = "";
            } else {
                String durStr;
                if (response.getPlaytimeSecondsMonth() == 0) {
                    durStr = "none";
                } else if (response.getPlaytimeSecondsMonth().equals(response.getPlaytimeSeconds())) {
                    durStr = "all";
                } else {
                    durStr = formatDuration(response.getPlaytimeSecondsMonth());
                }
                part2 = " - with %s in the last 30d".formatted(durStr);
            }
            return part1 + part2;
        } catch (ApiException e) {
            error("API error", e);
            return null;
        }
    }
}
