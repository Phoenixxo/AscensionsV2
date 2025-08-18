package dev.phoenixxo.requirements;

import dev.drawethree.xprison.api.XPrisonAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class PrestigeRequirement implements RebirthRequirement {
    private final int requiredPrestige;
    private final XPrisonAPI api;

    public PrestigeRequirement(String value, XPrisonAPI api) {
        this.requiredPrestige = Integer.parseInt(value);
        this.api = api;
    }

    @Override
    public boolean isMet(Player player) {
        int prestige = Math.toIntExact(api.getPrestigesApi().getPlayerPrestige(player).getId());
        return prestige >= requiredPrestige;
    }

    @Override
    public Component getDisplay(Player player) {

        String currentPrefix = api.getPrestigesApi().getPlayerPrestige(player).getPrefix();
        String requiredPrefix = api.getPrestigesApi().getPrestigeById(requiredPrestige).getPrefix();
        return MiniMessage.miniMessage().deserialize("<white>Prestige: </white>" + currentPrefix + "<white> / Required: </white>" + requiredPrefix);
    }

    private String convertLegacyToMiniMessage(String legacy) {
        return MiniMessage.miniMessage().serialize(
                LegacyComponentSerializer.legacySection().deserialize(legacy)
        );
    }

}

