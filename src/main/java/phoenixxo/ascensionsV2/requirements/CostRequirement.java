package phoenixxo.ascensionsV2.requirements;

import dev.drawethree.xprison.api.XPrisonAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import phoenixxo.ascensionsV2.AscensionsV2;
import phoenixxo.ascensionsV2.managers.AscensionLevelManager;

import java.io.File;

public class CostRequirement implements AscensionRequirement {
    private final double baseCost;
    private final double growthFactor;
    private final XPrisonAPI api;
    private final AscensionLevelManager levelManager;
    private final FileConfiguration config;


    public CostRequirement(String unused, XPrisonAPI api) {
        this.api = api;
        this.config = AscensionsV2.getInstance().getConfig();
        this.baseCost = config.getDouble("ascension.base_cost", 1000000);
        this.growthFactor = config.getDouble("ascension.cost_growth", 1.15);
        this.levelManager = AscensionsV2.getInstance().getAscensionLevelManager();
    }

    @Override
    public boolean isMet(Player player) {
        int level = levelManager.getAscensionLevel(player.getUniqueId());
        double required = baseCost * Math.pow(growthFactor, level);
        double balance = api.getCurrencyApi().getBalance(player, "money");
        return balance >= required;
    }

    @Override
    public Component getDisplay(Player player) {
        int level = levelManager.getAscensionLevel(player.getUniqueId());
        double required = baseCost * Math.pow(growthFactor, level);
        double balance = api.getCurrencyApi().getBalance(player, "money");
        String display = "<white>Cost: <green>$" + String.format("%,.2f", balance)
                + "</green> / Required: <yellow>$" + String.format("%,.2f", required) + "</yellow>";
        return MiniMessage.miniMessage().deserialize(display);
    }
}

