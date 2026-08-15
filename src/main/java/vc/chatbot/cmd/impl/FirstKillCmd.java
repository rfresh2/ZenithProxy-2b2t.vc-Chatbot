package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.util.List;

public class FirstKillCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("firstkill", "fk");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return noPlayerFoundMsg(targetName(sender, args));
        }
        try {
            var response = ApiClients.INSTANCE.deathsApi().killsWithHttpInfo(targetProfile.get().uuid(), targetProfile.get().name(), null, null, "asc", 1, null);
            if (response.getStatusCode() == 204 || response.getData() == null || response.getData().getKills().isEmpty()) {
                return "%s has never killed anyone".formatted(targetProfile.get().name());
            }
            var firstKill = response.getData().getKills().getFirst();
            return "[%s] %s".formatted(
                firstKill.getTime(),
                firstKill.getDeathMessage()
            );
        } catch (ApiException e) {
            error("API error", e);
            return null;
        }
    }
}
