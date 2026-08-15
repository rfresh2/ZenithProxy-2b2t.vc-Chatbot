package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

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
            return "Non-prio slots: %s - Prio slots: %s".formatted(
                response.getNonPrioCount(),
                response.getPrioCount()
            );
        } catch (ApiException e) {
            error("API error", e);
            return null;
        }
    }
}
