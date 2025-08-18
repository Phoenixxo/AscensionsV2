package dev.phoenixxo.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

public class ComponentUtil {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.legacySection();

    /**
     * Converts a legacy string (with &-codes) to an Adventure Component.
     * @param legacy The legacy string (e.g. "&7[&aA&7]")
     * @return A properly colored Component
     */
    public static Component fromLegacy(String legacy) {
        if (legacy == null) {
            return Component.empty();
        }
        String formatted = ChatColor.translateAlternateColorCodes('&', legacy);
        return LEGACY_SERIALIZER.deserialize(formatted);
    }

}
