package vc.bot.api;

import lombok.Getter;
import lombok.experimental.Accessors;
import vc.bot.BuildConstants;
import vc.bot.VcBotPlugin;
import vc.bot.openapi.handler.*;

import java.net.http.HttpClient;
import java.time.Duration;

import static com.zenith.Globals.CONFIG;
import static vc.bot.VcBotPlugin.PLUGIN_CONFIG;

@Getter
@Accessors(fluent = true)
public class ApiClients {
    public static final ApiClients INSTANCE = new ApiClients();

    private final DeathsApi deathsApi;
    private final SeenApi seenApi;
    private final ChatsApi chatsApi;
    private final StatsApi statsApi;
    private final PlaytimeApi playtimeApi;
    private final PriorityPlayersApi priorityPlayersApi;
    private final TabListApi tabListApi;
    private final TimeApi timeApi;
    private final BotsApi botsApi;

    private ApiClients() {
        var apiClient = apiClient();
        deathsApi = new DeathsApi(apiClient);
        seenApi = new SeenApi(apiClient);
        chatsApi = new ChatsApi(apiClient);
        statsApi = new StatsApi(apiClient);
        playtimeApi = new PlaytimeApi(apiClient);
        priorityPlayersApi = new PriorityPlayersApi(apiClient);
        tabListApi = new TabListApi(apiClient);
        timeApi = new TimeApi(apiClient);
        botsApi = new BotsApi(apiClient);
    }

    ApiClient apiClient() {
        return new ApiClient(
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
                b.header("User-Agent", "ZenithProxy-2b2t.vc-Chatbot/%s/%s".formatted(CONFIG.authentication.username, BuildConstants.VERSION));
            })
            .setResponseInterceptor(r -> {
                VcBotPlugin.LOG.info("{} -> {}", r.uri().toString(), r.statusCode());
            });
    }
}
