package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.util.List;

public class JoinCountCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("joins", "connections");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return noPlayerFoundMsg(targetName(sender, args));
        }
        try {
            var response = ApiClients.INSTANCE.statsApi().playerStatsWithHttpInfo(targetProfile.get().uuid(), targetProfile.get().name());
            if (response.getStatusCode() == 204 || response.getData() == null) {
                return noDataFoundMsg(targetProfile.get().name());
            }
            var jc = response.getData().getJoinCount();
            if (jc == 0) {
                return "%s has never joined".formatted(targetProfile.get().name());
            }
            return "%s has joined %s times".formatted(targetProfile.get().name(), jc);
        } catch (ApiException e) {
            error("API error", e);
            return null;
        }
    }
}
