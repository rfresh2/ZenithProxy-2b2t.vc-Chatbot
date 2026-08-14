package vc.chatbot.cmd.impl;

import com.zenith.feature.queue.Queue;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.util.List;

public class QueueCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("q", "queue");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        return "Queue: %s - Prio: %s - ETA: %s".formatted(
            Queue.getQueueStatus().regular(),
            Queue.getQueueStatus().prio(),
            Queue.getQueueEta(Queue.getQueueStatus().regular())
        );
    }
}
