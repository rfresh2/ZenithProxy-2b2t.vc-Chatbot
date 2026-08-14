package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.openapi.handler.ApiException;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;

import java.util.List;

public class KillDeathCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("kd", "kills", "deaths");
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
            return "%s has: %s kills, %s deaths, %.2f K/D".formatted(
                targetProfile.get().name(),
                response.getKillCount(),
                response.getDeathCount(),
                response.getDeathCount() == 0
                    ? response.getKillCount() == 0
                        ? 0
                        : 1.0
                    : response.getKillCount() / (double) response.getDeathCount()
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
