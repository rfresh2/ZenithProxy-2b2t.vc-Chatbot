package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.util.List;

public class PrioPlayersCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("prioplayers", "priocount", "priomoney", "money");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        try {
            var response = ApiClients.INSTANCE.priorityPlayersApi().priorityPlayers();
            if (response == null) {
                return noDataFoundMsg();
            }
            if (response.getPlayers() == null) {
                return noDataFoundMsg();
            }
            var count = response.getPlayers().size();
            var moneyPerMonth = count * 20;
            return "there are currently %s prio players, making 2b2t $%s this month".formatted(
                count,
                moneyPerMonth
            );
        } catch (ApiException e) {
            error("API error", e);
            return null;
        }
    }
}
