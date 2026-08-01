package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.TabListApi;

import java.util.List;

public class PrioSlotsCmd extends Cmd {
    private final TabListApi tabListApi = new TabListApi(apiClient());

    @Override
    public List<String> cmdAliases() {
        return List.of("prio", "prioslots", "slots");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var response = tabListApi.onlinePlayersInfo();
        if (response == null) {
            return null;
        }
        return "Non-prio: %s - Prio: %s".formatted(
            response.getNonPrioCount(),
            response.getPrioCount()
        );
    }
}
