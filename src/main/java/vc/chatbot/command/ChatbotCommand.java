package vc.chatbot.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import vc.chatbot.module.ChatbotModule;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.zenith.Globals.MODULE;
import static com.zenith.command.brigadier.CustomStringArgumentType.getString;
import static com.zenith.command.brigadier.CustomStringArgumentType.wordWithChars;
import static com.zenith.command.brigadier.TimeArgument.getTime;
import static com.zenith.command.brigadier.TimeArgument.time;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import static com.zenith.discord.DiscordBot.escape;
import static vc.chatbot.ChatbotPlugin.PLUGIN_CONFIG;

public class ChatbotCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("chatbot")
            .category(CommandCategory.MODULE)
            .description("""
             In-game 2b2t chatbot, powered by https://api.2b2t.vc
             
             You don't need an API key, but getting one may help avoid rate limits.
             """)
            .usageLines(
                "on/off",
                "publicChat on/off",
                "whispers on/off",
                "prefix <prefix>",
                "ignoreSelf on/off",
                "apiKey set <key>",
                "apiKey clear",
                "chatCooldown <ticks>",
                "response randomSuffix on/off",
                "response prefix on/off",
                "response prefix set <prefix>",
                "response retry on/off",
                "response retry attempts <attempts>"
            )
            .aliases("vc")
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("chatbot")
            .then(argument("toggle", toggle()).executes(c -> {
                PLUGIN_CONFIG.enabled = getToggle(c, "toggle");
                MODULE.get(ChatbotModule.class).syncEnabledFromConfig();
                c.getSource().getEmbed()
                    .title("Chatbot " + toggleStrCaps(PLUGIN_CONFIG.enabled));
            }))
            .then(literal("publicChat").then(argument("toggle", toggle()).executes(c -> {
                PLUGIN_CONFIG.publicChat = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title("Public Chat " + toggleStrCaps(PLUGIN_CONFIG.publicChat));
            })))
            .then(literal("whispers").then(argument("toggle", toggle()).executes(c -> {
                PLUGIN_CONFIG.whispers = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title("Whispers " + toggleStrCaps(PLUGIN_CONFIG.whispers));
            })))
            .then(literal("prefix").then(argument("prefix", wordWithChars()).executes(c -> {
                PLUGIN_CONFIG.prefix = getString(c, "prefix");
                c.getSource().getEmbed()
                    .title("Prefix Set");
            })))
            .then(literal("ignoreSelf").then(argument("toggle", toggle()).executes(c -> {
                PLUGIN_CONFIG.ignoreSelf = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title("Ignore Self " + toggleStrCaps(PLUGIN_CONFIG.ignoreSelf));
            })))
            .then(literal("apiKey")
                .then(literal("clear").executes(c -> {
                    PLUGIN_CONFIG.apiKey = "";
                    c.getSource().getEmbed()
                        .title("API Key Cleared");
                }))
                .then(literal("set").then(argument("key", wordWithChars()).executes(c -> {
                    PLUGIN_CONFIG.apiKey = getString(c, "key");
                    c.getSource().getEmbed()
                        .title("API Key Set");
                }))))
            .then(literal("chatCooldown").then(argument("ticks", time()).executes(c -> {
                PLUGIN_CONFIG.chatCooldownTicks = getTime(c, "ticks");
                c.getSource().getEmbed()
                    .title("Chat Cooldown Set");
            })))
            .then(literal("response")
                .then(literal("randomSuffix").then(argument("toggle", toggle()).executes(c -> {
                    PLUGIN_CONFIG.responseRandomSuffix = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title("Random Suffix " + toggleStrCaps(PLUGIN_CONFIG.responseRandomSuffix));
                })))
                .then(literal("prefix")
                    .then(argument("toggle", toggle()).executes(c -> {
                        PLUGIN_CONFIG.responsePrefixEnabled = getToggle(c, "toggle");
                        c.getSource().getEmbed()
                            .title("Response Prefix " + toggleStrCaps(PLUGIN_CONFIG.responsePrefixEnabled));
                    }))
                    .then(literal("set").then(argument("prefix", string()).executes(c -> {
                        PLUGIN_CONFIG.responsePrefix = getString(c, "prefix");
                        c.getSource().getEmbed()
                            .title("Response Prefix Set");
                    }))))
                .then(literal("retry")
                    .then(argument("toggle", toggle()).executes(c -> {
                        PLUGIN_CONFIG.responseRetry = getToggle(c, "toggle");
                        c.getSource().getEmbed()
                            .title("Response Retry " + toggleStrCaps(PLUGIN_CONFIG.responseRetry));
                    }))
                    .then(literal("attempts").then(argument("attempts", integer()).executes(c -> {
                        PLUGIN_CONFIG.responseMaxRetryAttempts = getInteger(c, "attempts");
                        c.getSource().getEmbed()
                            .title("Response Retry Attempts Set");
                    })))));
    }

    @Override
    public void defaultHandler(final CommandContext context) {
        context.getEmbed()
            .addField("Chatbot", toggleStr(PLUGIN_CONFIG.enabled))
            .addField("Public Chat", toggleStr(PLUGIN_CONFIG.publicChat))
            .addField("Whispers", toggleStr(PLUGIN_CONFIG.whispers))
            .addField("Prefix", "`" + escape(PLUGIN_CONFIG.prefix) + "`")
            .addField("Ignore Self", toggleStr(PLUGIN_CONFIG.ignoreSelf))
            .addField("API Key", PLUGIN_CONFIG.apiKey.isEmpty() ? "(none)" : escape(PLUGIN_CONFIG.apiKey))
            .addField("Chat Cooldown", "%s ticks (%.2f seconds)".formatted(
                PLUGIN_CONFIG.chatCooldownTicks,
                PLUGIN_CONFIG.chatCooldownTicks / 20.0
            ))
            .addField("Response Random Suffix", toggleStr(PLUGIN_CONFIG.responseRandomSuffix))
            .addField("Response Prefix", PLUGIN_CONFIG.responsePrefixEnabled ? "`" + escape(PLUGIN_CONFIG.responsePrefix) + "`" : "(disabled)")
            .addField("Response Retry", toggleStr(PLUGIN_CONFIG.responseRetry))
            .addField("Response Retry Attempts", PLUGIN_CONFIG.responseMaxRetryAttempts)
            .primaryColor();
    }

}
