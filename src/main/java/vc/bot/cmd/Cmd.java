package vc.bot.cmd;

import com.zenith.feature.api.ProfileData;
import com.zenith.feature.whitelist.PlayerListsManager;
import com.zenith.module.api.ModuleUtils;
import com.zenith.util.ChatUtil;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.jspecify.annotations.Nullable;
import vc.bot.openapi.handler.ApiException;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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

    record TablistProfileData(String name, UUID uuid) implements ProfileData { }

    public Optional<ProfileData> resolveProfileData(String targetName) {
        if (!ChatUtil.isValidPlayerName(targetName)) {
            return Optional.empty();
        }
        return CACHE.getTabListCache().getFromName(targetName)
            .map(p -> (ProfileData) new TablistProfileData(p.getName(), p.getProfileId()))
            .or(() -> PlayerListsManager.getProfileFromUsername(targetName));
    }

    public Optional<ProfileData> target(PlayerListEntry sender, List<String> args) {
        if (args.isEmpty()) {
            return Optional.of(new TablistProfileData(sender.getName(), sender.getProfileId()));
        }
        return resolveProfileData(args.getFirst());
    }
}
