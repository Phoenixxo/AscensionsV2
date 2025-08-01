package phoenixxo.ascensionsV2.util;


import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import phoenixxo.ascensionsV2.AscensionsV2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static FileConfiguration config;

    public static void init (AscensionsV2 plugin) {

        config = plugin.getMessagesManager().getMessagesConfig();
    }

    public static Component get(String key) {
        String raw = config.getString(key, "<red>Message missing: " + key);
        return mm.deserialize(raw);
    }

    public static Component get(String key, String placeholder, String value) {
        String raw = config.getString(key, "<red>Message missing: " + key);
        raw = raw.replace(placeholder, value);
        return mm.deserialize(raw);
    }

    public static Component get(String key, String... replacements) {
        String raw = config.getString(key, "<red>Message missing: " + key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            raw = raw.replace(replacements[i], replacements[i +1]);
        }
        return mm.deserialize(raw);
    }

    public static String expandAllPlaceholders (Player player, String text) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) return "";

        Pattern pattern = Pattern.compile("%+[^%]+%");
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group();
            String replacement = PlaceholderAPI.setPlaceholders(player, placeholder);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

}
