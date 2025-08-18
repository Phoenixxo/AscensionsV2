package dev.phoenixxo.util;


import dev.phoenixxo.BetterRebirth;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageUtil {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final DecimalFormat DF = new DecimalFormat("#.##");

    private static FileConfiguration config;

    public static void init (BetterRebirth plugin) {

        config = plugin.getMessagesManager().getMessagesConfig();
    }

    public static Component get(String key) {
        String raw = config.getString(key, "<red>Message missing: " + key);
        return MM.deserialize(raw);
    }

    public static Component get(String key, String placeholder, String value) {
        String raw = config.getString(key, "<red>Message missing: " + key);
        raw = raw.replace(placeholder, value);
        return MM.deserialize(raw);
    }

    public static Component get(String key, String... replacements) {
        String raw = config.getString(key, "<red>Message missing: " + key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            raw = raw.replace(replacements[i], replacements[i +1]);
        }
        return MM.deserialize(raw);
    }

    public static String expandAllPlaceholders (Player player, String text) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) return "";

        Pattern pattern = Pattern.compile("%+[^%]+%");
        Matcher matcher = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String placeholder = matcher.group();
            String replacement = PlaceholderAPI.setPlaceholders(player, placeholder);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }



    public static String fmtDouble(double v) {
        return DF.format(v);
    }

    public static Component guiTitle(int points) {
        String raw = config.getString("gui.title",
                "<gold>Rebirth Perks</gold>  <gray>(Points: <yellow>%points%</yellow>)</gray>");
        raw = raw.replace("%points%", String.valueOf(points));
        return MM.deserialize(raw);
    }

    public static String guiFillerNameRaw() {
        return config.getString("gui.filler_name", " ");
    }

    public static String guiConnectorsNameRaw() {
        return config.getString("gui.connectors_name", " ");
    }

    public static Component guiPointsLine(int points) {
        String raw = config.getString("gui.points", "<gray>Points:</gray> <yellow>%points%</yellow>");
        raw = raw.replace("%points%", String.valueOf(points));
        return MM.deserialize(raw);
    }

    public static Component guiCostPerRank(int cost) {
        String raw = config.getString("gui.cost_per_rank", "<gray>Cost per rank:</gray> <gold>%cost%</gold>");
        raw = raw.replace("%cost%", String.valueOf(cost));
        return MM.deserialize(raw);
    }

    public static Component guiValueNow(String bonusType, double value) {
        String formatted = formatValueRaw(bonusType, value);
        String raw = config.getString("gui.value_now", "<gray>Value now:</gray> %value%");
        raw = raw.replace("%value%", formatted);
        return MM.deserialize(raw);
    }

    public static Component guiValueNext(String bonusType, double value) {
        String formatted = formatValueRaw(bonusType, value);
        String raw = config.getString("gui.value_next", "<gray>Value next:</gray> %value%");
        raw = raw.replace("%value%", formatted);
        return MM.deserialize(raw);
    }

    public static Component guiRequiresHeader(List<String> renderedItems) {
        String list = renderedItems.isEmpty()
                ? "<gray>None</gray>"
                : renderedItems.stream().collect(Collectors.joining("<gray>, </gray>"));
        String raw = config.getString("gui.requires_header", "<gray>Requires:</gray> %list%");
        raw = raw.replace("%list%", list);
        return MM.deserialize(raw);
    }

    public static String guiRequiresItemRaw(boolean ok, String id) {
        String key = ok ? "gui.requires_item_ok" : "gui.requires_item_missing";
        String raw = config.getString(key, ok
                ? "<green>✓</green> <white>%id%</white>"
                : "<red>✗</red> <white>%id%</white>");
        return raw.replace("%id%", id);
    }

    public static Component guiUnlockedClickToRankUp() {
        String raw = config.getString("gui.unlocked_click_to_rankup", "<yellow>Click to rank up</yellow>");
        return MM.deserialize(raw);
    }

    public static Component guiUnlockedMax() {
        String raw = config.getString("gui.unlocked_max", "<green>Unlocked (Max)</green>");
        return MM.deserialize(raw);
    }

    public static Component guiAvailableClick() {
        String raw = config.getString("gui.available_click", "<yellow>Click to unlock</yellow>");
        return MM.deserialize(raw);
    }

    public static Component guiLocked() {
        String raw = config.getString("gui.locked", "<red>Locked</red>");
        return MM.deserialize(raw);
    }

    public static Component formatValue(String bonusType, double amount) {
        return MM.deserialize(formatValueRaw(bonusType, amount));
    }

    public static String formatValueRaw(String bonusType, double amount) {
        String key = "format.flat";
        if (bonusType != null) {
            switch (bonusType.toLowerCase()) {
                case "percentage" -> key = "format.percentage";
                case "multiplier" -> key = "format.multiplier";
                default -> key = "format.flat";
            }
        }
        String raw = config.getString(key, "<green>%amount%</green>");
        // for percentage, messages.yml expects percent sign appended in the template
        raw = raw.replace("%amount%", fmtDouble(amount));
        return raw;
    }

    public static Component msgUpgraded(String id) {
        String raw = config.getString("messages.upgraded", "<green>Upgraded:</green> <yellow>%id%</yellow>");
        return MM.deserialize(raw.replace("%id%", id));
    }

    public static Component msgUnknownNode() {
        return get("messages.unknown_node");
    }

    public static Component msgAlreadyMax() {
        return get("messages.already_max");
    }

    public static Component msgMissingRequirements() {
        return get("messages.missing_requirements");
    }

    public static Component msgNotEnoughPoints() {
        return get("messages.not_enough_points");
    }

    public static Component msgCannotUpgrade() {
        return get("messages.cannot_upgrade");
    }

}
