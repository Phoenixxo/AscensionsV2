package phoenixxo.ascensionsV2.requirements;

import dev.drawethree.xprison.api.XPrisonAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import phoenixxo.ascensionsV2.util.ComponentUtil;

public class PrestigeRequirement implements AscensionRequirement {

    private final int requiredPrestige;
    private final XPrisonAPI api;

    public PrestigeRequirement(String value, XPrisonAPI api) {
        this.requiredPrestige = Integer.parseInt(value);
        this.api = api;
    }

    @Override
    public boolean isMet(Player player) {
        return api.getPrestigesApi().getPlayerPrestige(player).getId() == requiredPrestige;
    }

    @Override
    public Component getDisplay(Player player) {
            return Component.text("Prestige: ", NamedTextColor.WHITE)
                    .append(ComponentUtil.fromLegacy(api.getPrestigesApi().getPlayerPrestige(player).getPrefix()))
                    .append(Component.text(" / Required: ", NamedTextColor.WHITE))
                    .append(ComponentUtil.fromLegacy(api.getPrestigesApi().getPrestigeById(requiredPrestige).getPrefix()));
        }

    }
