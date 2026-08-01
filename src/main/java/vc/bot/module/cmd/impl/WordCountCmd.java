package vc.bot.module.cmd.impl;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.module.cmd.Cmd;
import vc.bot.openapi.handler.ApiException;
import vc.bot.openapi.handler.ChatsApi;

import java.time.Duration;
import java.util.List;

public class WordCountCmd extends Cmd {
    private final ChatsApi chatsApi = new ChatsApi(apiClient().setReadTimeout(Duration.ofSeconds(60)));

    @Override
    public List<String> cmdAliases() {
        return List.of("wc", "wordcount", "clout", "word");
    }

    @Override
    public @Nullable String handle(final PlayerListEntry sender, final List<String> args) throws ApiException {
        if (args.isEmpty()) {
            return null;
        }
        var word = String.join(" ", args);
        if (word.isBlank()) {
            return null;
        }
        if (word.length() < 3 || word.length() > 16) {
            return "Invalid word length, 4 <= length <= 16";
        }
        try {
            var response = chatsApi.wordCount(word);
            if (response == null) {
                return null;
            }
            if (response.getCount() == null) {
                return "No data found";
            }
            return "the word '%s' has been seen %s times!".formatted(
                word,
                response.getCount()
            );
        } catch (ApiException e) {
            if (e.getCode() == 204) {
                return "No data found";
            }
            error("API error", e);
            return null;
        }
    }
}
