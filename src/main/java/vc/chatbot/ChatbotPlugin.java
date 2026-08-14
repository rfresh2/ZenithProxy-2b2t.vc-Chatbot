package vc.chatbot;

import com.zenith.plugin.api.Plugin;
import com.zenith.plugin.api.PluginAPI;
import com.zenith.plugin.api.ZenithProxyPlugin;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import vc.chatbot.command.ChatbotCommand;
import vc.chatbot.module.ChatbotModule;

@Plugin(
    id = BuildConstants.PLUGIN_ID,
    version = BuildConstants.VERSION,
    description = "In-game 2b2t Chatbot, powered by https://api.2b2t.vc",
    url = "https://github.com/rfresh2/ZenithProxy-2b2t.vc-Chatbot",
    authors = {"rfresh2"},
    mcVersions = {"*"}
)
public class ChatbotPlugin implements ZenithProxyPlugin {
    public static ChatbotConfig PLUGIN_CONFIG;
    public static ComponentLogger LOG;

    @Override
    public void onLoad(final PluginAPI pluginAPI) {
        LOG = pluginAPI.getLogger();
        // initialize any configurations before modules or commands might need to read them
        PLUGIN_CONFIG = pluginAPI.registerConfig(BuildConstants.PLUGIN_ID, ChatbotConfig.class);
        pluginAPI.registerModule(new ChatbotModule());
        pluginAPI.registerCommand(new ChatbotCommand());
    }
}
