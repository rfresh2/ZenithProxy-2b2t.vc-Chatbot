package vc.bot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.api.ApiClients;
import vc.bot.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;

import java.util.List;

public class PrioSlotsCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("prio", "prioslots", "slots");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        try {
            var response = ApiClients.INSTANCE.tabListApi().onlinePlayersInfo();
            if (response == null) {
                return noDataFoundMsg();
            }
            return "Non-prio: %s - Prio: %s".formatted(
                response.getNonPrioCount(),
                response.getPrioCount()
            );
        } catch (ApiException e) {
            if (e.getCode() == 204) {
                return noDataFoundMsg();
            }
            error("API error", e);
            return null;
        }
    }
}
