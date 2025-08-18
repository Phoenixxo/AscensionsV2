package dev.phoenixxo.util;


import dev.phoenixxo.BetterRebirth;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RebirthExpansion extends PlaceholderExpansion {

    private final BetterRebirth plugin;
    private final Map<String, Function<Player, String>> placeholders = new HashMap<>();

    public RebirthExpansion(BetterRebirth plugin) {
        this.plugin = plugin;
        registerPlaceholders();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "rebirth";
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
        placeholders.put("level", p -> String.valueOf(plugin.getRebirthLevelManager().getRebirthLevel(p.getUniqueId())));
        placeholders.put("next_level", p -> String.valueOf(plugin.getRebirthLevelManager().getRebirthLevel(p.getUniqueId())) + 1);
        placeholders.put("can_ascend", p -> String.valueOf(plugin.getRebirthManager().canRebirth(p)));
        placeholders.put("progress", p -> {
            long met = plugin.getRebirthManager().getRequirements().stream().filter(r -> r.isMet(p)).count();
            int total = plugin.getRebirthManager().getRequirements().size();
            return met + "/" + total;
        });
        placeholders.put("ready", p -> plugin.getRebirthManager().canRebirth(p) ? "Ready!" : "Not Ready");
        placeholders.put("status_colored", p -> plugin.getRebirthManager().canRebirth(p) ? "&a✓" : "&c✗");
        placeholders.put("prefix", p -> MiniMessage.miniMessage().serialize(
                plugin.getPrefixManager().getPrefixComponent(p, plugin.getRebirthLevelManager().getRebirthLevel(p.getUniqueId())
                )
        ));
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.isOnline()) return "";
        Function<Player, String> resolver = placeholders.get(params.toLowerCase());
        return resolver != null ? resolver.apply(player.getPlayer()) : null;
    }
}
