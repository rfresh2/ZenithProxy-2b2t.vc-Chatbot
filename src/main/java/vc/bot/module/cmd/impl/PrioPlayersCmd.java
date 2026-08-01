package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.PriorityPlayersApi;

import java.util.List;

public class PrioPlayersCmd extends Cmd {
    private final PriorityPlayersApi priorityPlayersApi = new PriorityPlayersApi(apiClient());

    @Override
    public List<String> cmdAliases() {
        return List.of("prioplayers", "priocount", "priomoney", "money");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var response = priorityPlayersApi.priorityPlayers();
        if (response == null || response.getPlayers() == null) {
            return null;
        }
        var count = response.getPlayers().size();
        var moneyPerMonth = count * 20;
        return "there are currently %s prio players, making 2b2t $%s this month".formatted(
            count,
            moneyPerMonth
        );
    }
}
