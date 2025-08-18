package dev.phoenixxo.requirements;

import dev.drawethree.xprison.api.XPrisonAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class RankRequirement implements RebirthRequirement {
    private final int requiredRankId; // You could also use String for rank name/letter
    private final XPrisonAPI api;

    public RankRequirement(String value, XPrisonAPI api) {
        this.requiredRankId = Integer.parseInt(value);
        this.api = api;
    }

    @Override
    public boolean isMet(Player player) {
        int playerRank = api.getRanksApi().getPlayerRank(player).getId();
        return playerRank >= requiredRankId;
    }

    @Override
    public Component getDisplay(Player player) {
        String display = "<white>Rank: </white>"
                + api.getRanksApi().getPlayerRank(player).getPrefix()
                + "<white> / Required: </white>"
                + api.getRanksApi().getRankById(requiredRankId).getPrefix();
        return MiniMessage.miniMessage().deserialize(display);
    }
}
