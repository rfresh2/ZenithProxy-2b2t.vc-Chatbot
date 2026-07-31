package vc.bot.module.cmd;

import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.BuildConstants;
import vc.bot.openapi.handler.ApiClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import static com.zenith.Globals.CONFIG;
import static vc.bot.VcBotPlugin.PLUGIN_CONFIG;

public abstract class Cmd {
    public abstract List<String> cmdAliases();
    public abstract @Nullable String handle(PlayerListEntry sender, List<String> args);

    private static final DateTimeFormatter defaultDateFormatter = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd HH:mm:ss")
        .toFormatter();
    public DateTimeFormatter defaultDateFormatter() {
        return defaultDateFormatter;
    }

    private static final ApiClient defaultApiClient = new ApiClient(
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)),
        ApiClient.createDefaultObjectMapper(),
        "https://api.2b2t.vc")
        .setReadTimeout(Duration.ofSeconds(30))
        .setRequestInterceptor(b -> {
            var apiKey = PLUGIN_CONFIG.apiKey;
            if (!apiKey.isEmpty()) {
                b.header("X-API-Key", apiKey);
            }
            b.header("User-Agent", "ZenithProxy-2b2t.vc-Chatbot/" + CONFIG.authentication.username + "/" + BuildConstants.VERSION);
        });
    public ApiClient apiClient() {
        return defaultApiClient;
    }
}
