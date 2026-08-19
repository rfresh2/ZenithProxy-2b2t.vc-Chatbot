package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

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
            var response = ApiClients.INSTANCE.seenApi().seenWithHttpInfo(targetProfile.get().uuid(), targetProfile.get().name());
            if (response.getStatusCode() == 204 || response.getData() == null) {
                return "%s has never been seen on 2b2t".formatted(targetProfile.get().name());
            }
            var firstSeen = response.getData().getFirstSeen();
            var now = OffsetDateTime.now();
            var dateFmt = dateTimeFormatter()
                .format(firstSeen);
            return "%s was first seen on %s - %s ago".formatted(
                targetProfile.get().name(),
                dateFmt,
                formatDuration(firstSeen, now)
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
