package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.openapi.handler.ApiException;
import vc.chatbot.cmd.Cmd;

import java.util.List;

public class ReportCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("report");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return noPlayerFoundMsg(targetName(sender, args));
        }
        return "%s has been reported to the admins!".formatted(targetProfile.get().name());
    }
}
