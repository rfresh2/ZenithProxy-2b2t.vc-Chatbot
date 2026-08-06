package vc.bot.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import vc.bot.module.VcChatbotModule;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.zenith.Globals.MODULE;
import static com.zenith.command.brigadier.CustomStringArgumentType.getString;
import static com.zenith.command.brigadier.CustomStringArgumentType.wordWithChars;
import static com.zenith.command.brigadier.TimeArgument.getTime;
import static com.zenith.command.brigadier.TimeArgument.time;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import static com.zenith.discord.DiscordBot.escape;
import static vc.bot.VcBotPlugin.PLUGIN_CONFIG;

public class VcChatbotCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("vc")
            .category(CommandCategory.MODULE)
            .description("""
             In-game 2b2t chatbot, powered by https://api.2b2t.vc
             
             You don't need an API key, but getting one may help avoid rate limits.
             """)
            .usageLines(
                "on/off",
                "prefix <prefix>",
                "apiKey set <key>",
                "apiKey clear",
                "chatCooldown <ticks>",
                "appendRandom on/off",
                "responsePrefix on/off",
                "responsePrefix set <prefix>"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("vc")
            .then(argument("toggle", toggle()).executes(c -> {
                PLUGIN_CONFIG.enabled = getToggle(c, "toggle");
                MODULE.get(VcChatbotModule.class).syncEnabledFromConfig();
                c.getSource().getEmbed()
                    .title("Chatbot " + toggleStrCaps(PLUGIN_CONFIG.enabled));
            }))
            .then(literal("prefix").then(argument("prefix", wordWithChars()).executes(c -> {
                PLUGIN_CONFIG.prefix = getString(c, "prefix");
                c.getSource().getEmbed()
                    .title("Prefix Set");
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
            .then(literal("appendRandom").then(argument("toggle", toggle()).executes(c -> {
                PLUGIN_CONFIG.appendRandomSuffix = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title("Append Random Set");
            })))
            .then(literal("responsePrefix")
                .then(argument("toggle", toggle()).executes(c -> {
                    PLUGIN_CONFIG.responsePrefixEnabled = getToggle(c, "toggle");
                    c.getSource().getEmbed()
                        .title("Response Prefix " + toggleStrCaps(PLUGIN_CONFIG.responsePrefixEnabled));
                }))
                .then(literal("set").then(argument("prefix", greedyString()).executes(c -> {
                    PLUGIN_CONFIG.responsePrefix = getString(c, "prefix");
                    c.getSource().getEmbed()
                        .title("Response Prefix Set");
                }))));
    }

    @Override
    public void defaultHandler(final CommandContext context) {
        context.getEmbed()
            .addField("Chatbot", toggleStr(PLUGIN_CONFIG.enabled))
            .addField("Prefix", escape(PLUGIN_CONFIG.prefix))
            .addField("API Key", PLUGIN_CONFIG.apiKey.isEmpty() ? "(none)" : escape(PLUGIN_CONFIG.apiKey))
            .addField("Chat Cooldown", "%s ticks (%.2f seconds)".formatted(
                PLUGIN_CONFIG.chatCooldownTicks,
                PLUGIN_CONFIG.chatCooldownTicks / 20.0
            ))
            .addField("Append Random", toggleStr(PLUGIN_CONFIG.appendRandomSuffix))
            .addField("Response Prefix", PLUGIN_CONFIG.responsePrefixEnabled ? escape(PLUGIN_CONFIG.responsePrefix) : "(disabled)")
            .primaryColor();
    }

}
