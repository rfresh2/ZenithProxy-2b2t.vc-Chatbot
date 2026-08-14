package vc.chatbot.cmd;

import com.zenith.feature.api.ProfileData;
import com.zenith.feature.whitelist.PlayerListsManager;
import com.zenith.module.api.ModuleUtils;
import com.zenith.util.ChatUtil;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.openapi.handler.ApiException;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.zenith.Globals.CACHE;

public abstract class Cmd extends ModuleUtils {
    public abstract List<String> cmdAliases();
    public abstract @Nullable String handle(PlayerListEntry sender, List<String> args) throws ApiException;

    private static final DateTimeFormatter defaultDateFormatter = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd HH:mm:ss")
        .toFormatter();
    public DateTimeFormatter dateTimeFormatter() {
        return defaultDateFormatter;
    }

    public record TablistProfileData(String name, UUID uuid) implements ProfileData { }

    public Optional<ProfileData> resolveProfileData(String targetName) {
        if (!ChatUtil.isValidPlayerName(targetName)) {
            return Optional.empty();
        }
        return CACHE.getTabListCache().getFromName(targetName)
            .map(p -> (ProfileData) new TablistProfileData(p.getName(), p.getProfileId()))
            .or(() -> PlayerListsManager.getProfileFromUsername(targetName));
    }

    public String targetName(PlayerListEntry sender, List<String> args) {
        if (args.isEmpty()) {
            return sender.getName();
        }
        return args.getFirst();
    }

    public Optional<ProfileData> target(PlayerListEntry sender, List<String> args) {
        if (args.isEmpty()) {
            return Optional.of(new TablistProfileData(sender.getName(), sender.getProfileId()));
        }
        return resolveProfileData(args.getFirst());
    }

    public String formatDuration(long durationInSeconds) {
        var secondsInMinute = 60L;
        var secondsInHour = secondsInMinute * 60L;
        var secondsInDay = secondsInHour * 24L;
        var secondsInMonth = secondsInDay * 30L; // assuming 30 days per month
        var secondsInYear = secondsInMonth * 12L;

        var years = durationInSeconds / secondsInYear;
        var months = (durationInSeconds % secondsInYear) / secondsInMonth;
        var days = (durationInSeconds % secondsInMonth) / secondsInDay;
        var hours = (durationInSeconds % secondsInDay) / secondsInHour;
        List<String> entries = new ArrayList<>(4);
        if (years > 0) entries.add(years + "y");
        if (months > 0) entries.add(months + "mo");
        if (days > 0) entries.add(days + "d");
        if (hours > 0) entries.add(hours + "h");
        if (entries.isEmpty()) {
            var minutes = (double) durationInSeconds / (double)secondsInMinute;
            if (minutes == 0.0) {
                return "0m";
            }
            return String.format("%.2fm", minutes);
        }
        return String.join(" ", entries);
    }

    public String noPlayerFoundMsg(String input) {
        return "No player with that name exists";
    }

    public String noDataFoundMsg() {
        return "No data found";
    }

    public String noDataFoundMsg(String player) {
        return "No data found about %s".formatted(player);
    }
}
