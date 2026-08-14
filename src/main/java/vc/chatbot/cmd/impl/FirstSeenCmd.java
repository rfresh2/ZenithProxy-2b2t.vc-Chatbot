package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.openapi.handler.ApiException;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public class FirstSeenCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("joindate", "join", "jd", "firstseen");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return noPlayerFoundMsg(targetName(sender, args));
        }
        try {
            var response = ApiClients.INSTANCE.seenApi().seen(targetProfile.get().uuid(), targetProfile.get().name());
            if (response == null) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            var firstSeen = response.getFirstSeen();
            if (firstSeen == null) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            var dateFmt = dateTimeFormatter()
                .format(firstSeen);
            var sinceNow = Duration.between(firstSeen, OffsetDateTime.now());
            return "%s was first seen on %s - %s ago".formatted(
                targetProfile.get().name(),
                dateFmt,
                formatDuration(sinceNow.toSeconds())
            );
        } catch (ApiException e) {
            if (e.getCode() == 204) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            error("API error", e);
            return null;
        }
    }
}
