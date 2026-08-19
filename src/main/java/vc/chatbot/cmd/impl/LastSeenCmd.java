package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.time.OffsetDateTime;
import java.util.List;

public class LastSeenCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("seen", "lastseen");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return noPlayerFoundMsg(targetName(sender, args));
        }
        if (targetProfile.get() instanceof TablistProfileData) {
            return "%s is online now".formatted(targetProfile.get().name());
        }
        try {
            var response = ApiClients.INSTANCE.seenApi().seenWithHttpInfo(targetProfile.get().uuid(), targetProfile.get().name());
            if (response.getStatusCode() == 204 || response.getData() == null) {
                return "%s has never been seen on 2b2t".formatted(targetProfile.get().name());
            }
            var lastSeen = response.getData().getLastSeen();
            var now = OffsetDateTime.now();
            return "%s was last seen on %s - %s ago".formatted(
                targetProfile.get().name(),
                dateTimeFormatter().format(lastSeen),
                formatDuration(lastSeen, now)
            );
        } catch (ApiException e) {
            error("API error", e);
            return null;
        }
    }
}
