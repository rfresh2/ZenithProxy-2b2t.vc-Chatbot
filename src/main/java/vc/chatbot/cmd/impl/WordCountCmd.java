package vc.chatbot.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.chatbot.api.ApiClients;
import vc.chatbot.cmd.Cmd;
import vc.chatbot.openapi.handler.ApiException;

import java.util.List;

public class WordCountCmd extends Cmd {
    @Override
    public List<String> cmdAliases() {
        return List.of("wc", "wordcount", "clout", "word");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        if (args.isEmpty()) {
            return "No word provided!";
        }
        var word = String.join(" ", args);
        if (word.isBlank()) {
            return "No word provided!";
        }
        if (word.length() < 3 || word.length() > 16) {
            return "Invalid word length, 4 <= length <= 16";
        }
        try {
            var response = ApiClients.INSTANCE.chatsApi().wordCount(word);
            if (response == null) {
                return noDataFoundMsg();
            }
            if (response.getCount() == null) {
                return noDataFoundMsg();
            }
            return "the word '%s' has been seen %s times!".formatted(
                word,
                response.getCount()
            );
        } catch (ApiException e) {
            error("API error", e);
            return null;
        }
    }
}
