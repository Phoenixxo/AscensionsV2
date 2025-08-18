package dev.phoenixxo.managers;

import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.requirements.RebirthRequirement;
import dev.phoenixxo.requirements.RequirementFactory;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RebirthManager {

    @Getter
    private final List<RebirthRequirement> requirements = new ArrayList<>();

    private final BetterRebirth plugin;
    private final YamlConfiguration config;
    private final RequirementFactory requirementFactory;

    public RebirthManager(BetterRebirth plugin) {
        this.plugin = plugin;
        this.config = (YamlConfiguration) plugin.getConfig();
        this.requirementFactory = plugin.getRequirementFactory();
    }


    public void loadRequirements() {
        requirements.clear();
        List<Map<?, ?>> reqs = plugin.getConfig().getMapList("rebirth.requirements");

        for (Map<?, ?> entry : reqs) {
            String type = String.valueOf(entry.get("type"));
            boolean enabled = !entry.containsKey("enabled") || Boolean.parseBoolean(String.valueOf(entry.get("enabled")));
            if (!enabled) continue;
            String value = String.valueOf(entry.get("value"));
            RebirthRequirement req = requirementFactory.create(type, value);
            if (req != null) {
                requirements.add(req);
            } else {
                plugin.getLogger().warning("Invalid requirement type: " + type);
            }
        }
        plugin.getLogger().info("Loaded " + requirements.size() + " rebirth requirements.");
        }

    public boolean canRebirth(Player player) {
        return this.requirements.stream().allMatch(req -> req.isMet(player));
    }

    public void performRebirth(Player player) {
        if (!canRebirth(player)) {
            return;
        }

        if (config.getBoolean("rebirth.reset.rank", true)) {
            plugin.getPrisonAPI().getRanksApi().resetPlayerRank(player);
        }

        if (config.getBoolean("rebirth.reset.prestige", true)) {
            plugin.getPrisonAPI().getPrestigesApi().setPlayerPrestige(player, 0);
        }

        if (config.getBoolean("rebirth.reset.balance", true)) {
            plugin.getPrisonAPI().getCurrencyApi().setBalance(player, "money", 0);
        }

        int pointsGained = config.getInt("rebirth.points-per-rebirth", 1);
        plugin.getRebirthLevelManager().incrementLevel(player.getUniqueId());
        var data = plugin.getPlayerDataManager().get(player.getUniqueId());
        data.setSpentPoints(data.getSpentPoints() + pointsGained);
        plugin.getPlayerDataManager().save(player.getUniqueId());


    }

    public void reloadRequirements() {
        plugin.reloadConfig();
        loadRequirements();
    }

}
