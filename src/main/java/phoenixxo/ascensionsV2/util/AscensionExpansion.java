package phoenixxo.ascensionsV2.util;


import java.util.*;
import java.util.function.Function;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoenixxo.ascensionsV2.AscensionsV2;

public class AscensionExpansion extends PlaceholderExpansion {

    private final AscensionsV2 plugin;
    private final Map<String, Function<Player, String>> placeholders = new HashMap<>();

    public AscensionExpansion(AscensionsV2 plugin) {
        this.plugin = plugin;
        registerPlaceholders();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ascensions";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Phoenixxo";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    private void registerPlaceholders() {
        placeholders.put("level", p -> String.valueOf(plugin.getAscensionLevelManager().getAscensionLevel(p.getUniqueId())));
        placeholders.put("next_level", p -> String.valueOf(plugin.getAscensionLevelManager().getAscensionLevel(p.getUniqueId())) + 1);
        placeholders.put("can_ascend", p -> String.valueOf(plugin.getAscensionManager().canAscend(p)));
        placeholders.put("progress", p -> {
            long met = plugin.getAscensionManager().getRequirements().stream().filter(r -> r.isMet(p)).count();
            int total = plugin.getAscensionManager().getRequirements().size();
            return met + "/" + total;
        });
        placeholders.put("ready", p -> plugin.getAscensionManager().canAscend(p) ? "Ready!" : "Not Ready");
        placeholders.put("status_colored", p -> plugin.getAscensionManager().canAscend(p) ? "&a✓" : "&c✗");
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.isOnline()) return "";
        Function<Player, String> resolver = placeholders.get(params.toLowerCase());
        return resolver != null ? resolver.apply(player.getPlayer()) : null;
    }
}
