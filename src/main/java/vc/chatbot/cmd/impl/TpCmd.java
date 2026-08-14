package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TpCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("tp");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        var targetProfile = target(sender, args);
        if (targetProfile.isEmpty()) {
            return noPlayerFoundMsg(targetName(sender, args));
        }
        return "%s has been teleported to %s %s".formatted(
            targetProfile.get().name(),
            ThreadLocalRandom.current().nextInt(-30_000_000, 30_000_000),
            ThreadLocalRandom.current().nextInt(-30_000_000, 30_000_000)
        );
    }
}
