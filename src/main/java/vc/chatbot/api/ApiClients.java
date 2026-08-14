package vc.chatbot.api;

import lombok.Getter;
import lombok.experimental.Accessors;
import vc.bot.openapi.handler.*;
import vc.chatbot.BuildConstants;
import vc.chatbot.ChatbotPlugin;

import java.time.Duration;

import static com.zenith.Globals.CONFIG;
import static vc.chatbot.ChatbotPlugin.PLUGIN_CONFIG;

@Getter
@Accessors(fluent = true)
public class ApiClients {
    public static final ApiClients INSTANCE = new ApiClients();

    {
        Configuration.setDefaultApiClient(apiClient());
    }

    private final DeathsApi deathsApi = new DeathsApi();
    private final SeenApi seenApi = new SeenApi();
    private final ChatsApi chatsApi = new ChatsApi();
    private final StatsApi statsApi = new StatsApi();
    private final PlaytimeApi playtimeApi = new PlaytimeApi();
    private final PriorityPlayersApi priorityPlayersApi = new PriorityPlayersApi();
    private final TabListApi tabListApi = new TabListApi();
    private final TimeApi timeApi = new TimeApi();
    private final BotsApi botsApi = new BotsApi();

    static ApiClient apiClient() {
        return new ApiClient()
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(30))
            .setRequestInterceptor(req -> {
                var apiKey = PLUGIN_CONFIG.apiKey;
                if (!apiKey.isEmpty()) {
                    req.header("X-API-Key", apiKey);
                }
                req.header("User-Agent", "%s/%s/%s".formatted(
                    BuildConstants.PLUGIN_NAME,
                    CONFIG.authentication.username,
                    BuildConstants.VERSION
                ));
            })
            .setResponseInterceptor(res -> {
                ChatbotPlugin.LOG.info("{} -> {}", res.uri().toString(), res.statusCode());
            });
    }
}
