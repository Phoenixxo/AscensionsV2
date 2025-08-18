package dev.phoenixxo.requirements;

import dev.drawethree.xprison.api.XPrisonAPI;
import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.managers.RebirthLevelManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CostRequirement implements RebirthRequirement {
    private final double baseCost;
    private final double growthFactor;
    private final XPrisonAPI api;
    private final RebirthLevelManager levelManager;
    private final FileConfiguration config;


    public CostRequirement(String unused, XPrisonAPI api) {
        this.api = api;
        this.config = BetterRebirth.getInstance().getConfig();
        this.baseCost = config.getDouble("rebirth.base_cost", 1000000);
        this.growthFactor = config.getDouble("rebirth.cost_growth", 1.15);
        this.levelManager = BetterRebirth.getInstance().getRebirthLevelManager();
    }

    @Override
    public boolean isMet(Player player) {
        int level = levelManager.getRebirthLevel(player.getUniqueId());
        double required = baseCost * Math.pow(growthFactor, level);
        double balance = api.getCurrencyApi().getBalance(player, "money");
        return balance >= required;
    }

    @Override
    public Component getDisplay(Player player) {
        int level = levelManager.getRebirthLevel(player.getUniqueId());
        double required = baseCost * Math.pow(growthFactor, level);
        double balance = api.getCurrencyApi().getBalance(player, "money");
        String display = "<white>Cost: <green>$" + String.format("%,.2f", balance)
                + "</green> / Required: <yellow>$" + String.format("%,.2f", required) + "</yellow>";
        return MiniMessage.miniMessage().deserialize(display);
    }
}

