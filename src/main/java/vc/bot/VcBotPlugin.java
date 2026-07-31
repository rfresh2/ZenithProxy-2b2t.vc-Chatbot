package vc.bot;

import com.zenith.plugin.api.Plugin;
import com.zenith.plugin.api.PluginAPI;
import com.zenith.plugin.api.ZenithProxyPlugin;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import vc.bot.command.VcChatbotCommand;
import vc.bot.module.VcChatbotModule;

@Plugin(
    id = BuildConstants.PLUGIN_ID,
    version = BuildConstants.VERSION,
    description = "2b2t.vc Chatbot",
    url = "https://github.com/rfresh2/ZenithProxy-2b2t.vc-Chatbot",
    authors = {"rfresh2"},
    mcVersions = {"*"}
)
public class VcBotPlugin implements ZenithProxyPlugin {
    public static VcBotConfig PLUGIN_CONFIG;
    public static ComponentLogger LOG;

    @Override
    public void onLoad(final PluginAPI pluginAPI) {
        LOG = pluginAPI.getLogger();
        // initialize any configurations before modules or commands might need to read them
        PLUGIN_CONFIG = pluginAPI.registerConfig(BuildConstants.PLUGIN_ID, VcBotConfig.class);
        pluginAPI.registerModule(new VcChatbotModule());
        pluginAPI.registerCommand(new VcChatbotCommand());
    }
}
