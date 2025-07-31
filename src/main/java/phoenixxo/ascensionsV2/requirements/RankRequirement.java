package phoenixxo.ascensionsV2.requirements;

import dev.drawethree.xprison.api.XPrisonAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import phoenixxo.ascensionsV2.util.ComponentUtil;

public class RankRequirement implements AscensionRequirement {

    private final int requiredRank;
    private final XPrisonAPI api;

    public RankRequirement(String value, XPrisonAPI api) {
        try {
            this.requiredRank = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid rank ID: " + value);
        }
        this.api = api;
    }

    @Override
    public boolean isMet(Player player) {
        return api.getRanksApi().getPlayerRank(player).getId() == requiredRank;
    }

    @Override
    public Component getDisplay(Player player) {
        return Component.text("Rank: ", NamedTextColor.WHITE)
                .append(ComponentUtil.fromLegacy(api.getRanksApi().getPlayerRank(player).getPrefix()))
                .append(Component.text(" / Required: ", NamedTextColor.WHITE))
                .append(ComponentUtil.fromLegacy(api.getRanksApi().getRankById(requiredRank).getPrefix()));
    }

}
