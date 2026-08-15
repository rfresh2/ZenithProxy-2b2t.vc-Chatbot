package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.util.List;

public class BotCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("bot", "bots", "botcount");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        try {
            var tablistInfoResponse = ApiClients.INSTANCE.tabListApi().onlinePlayersInfo();
            if (tablistInfoResponse == null) {
                return noDataFoundMsg();
            }
            var botsResponse = ApiClients.INSTANCE.botsApi().botsMonth();
            if (botsResponse == null) {
                return noDataFoundMsg();
            }
            int botsOnlineNow = tablistInfoResponse.getBotCount();
            int monthlyBotCount = botsResponse.getPlayers().size();
            return "there are %s bots online now, and %s bots identified in the past 30d".formatted(
                botsOnlineNow,
                monthlyBotCount
            );
        } catch (ApiException e) {
            error("API error", e);
            return null;
        }
    }
}
