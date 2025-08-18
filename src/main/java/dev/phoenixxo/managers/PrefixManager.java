package dev.phoenixxo.managers;

import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrefixManager {

    private static class PrefixTier {
        final int min, max;
        final String name, open, close;
        final boolean showRoman;

        PrefixTier(int min, int max, String name, String open, String close, boolean showRoman) {
            this.min = min;
            this.max = max;
            this.name = name;
            this.open = open;
            this.close = close;
            this.showRoman = showRoman;
        }

        boolean matches(int level) {
            return level >= min && level <= max;
        }
    }

    private final List<PrefixTier> tiers = new ArrayList<>();
    public FileConfiguration config;
    public File file;

    public PrefixManager(BetterRebirth plugin) {
        plugin.saveResource("prefixes.yml", false);
        this.load(plugin);
    }

    public Component getPrefixComponent(Player player, int level) {
        for (PrefixTier tier : tiers) {
            if (tier.matches(level)) {

                int roman = Math.min(level - tier.min + 1, 5);
                String name = tier.name + (tier.showRoman ? " " + toRoman(roman) : "");
                String expanded = MessageUtil.expandAllPlaceholders(player, name);
                String styled = tier.open + expanded + tier.close;
                return MiniMessage.miniMessage().deserialize(styled);
            }
        }

        return Component.empty();
    }

    public String toRoman(int num) {
        return switch (num) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> "";
        };
    }

    public void load(BetterRebirth plugin) {
        tiers.clear();
        file = new File(plugin.getDataFolder(), "prefixes.yml");
        config = YamlConfiguration.loadConfiguration(file);
        for (Map<?, ?> section : config.getMapList("prefixes")) {
            int min = (int) section.get("min");
            int max = (int) section.get("max") != -1 ? (int) section.get("max") : Integer.MAX_VALUE;
            String name = section.get("name").toString();
            String open = section.get("open_style") != null ? section.get("open_style").toString() : "";
            String close = section.get("close_style") != null ? section.get("close_style").toString() : "";

            boolean showRoman = section.get("show_roman") != null && (boolean) section.get("show_roman");
            tiers.add(new PrefixTier(min, max, name, open, close, showRoman));
        }
    }

    public void reload(BetterRebirth plugin) {
        load(plugin);
    }

}
