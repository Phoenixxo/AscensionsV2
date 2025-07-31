package phoenixxo.ascensionsV2.managers;

import dev.drawethree.xprison.api.XPrisonAPI;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import phoenixxo.ascensionsV2.AscensionsV2;
import phoenixxo.ascensionsV2.requirements.AscensionRequirement;
import phoenixxo.ascensionsV2.requirements.RequirementFactory;
import phoenixxo.ascensionsV2.util.MessageUtil;

import java.util.*;

public class AscensionManager {

    private final List<AscensionRequirement> requirements = new ArrayList<>();

    private final AscensionsV2 plugin;
    private final YamlConfiguration config;
    private final RequirementFactory requirementFactory;

    public AscensionManager(AscensionsV2 plugin) {
        this.plugin = plugin;
        this.config = (YamlConfiguration) plugin.getConfig();
        this.requirementFactory = plugin.getRequirementFactory();
    }


    public void loadRequirements() {
        requirements.clear();
        List<Map<?, ?>> reqs = plugin.getConfig().getMapList("ascension.requirements");

        for (Map<?, ?> entry : reqs) {
            String type = String.valueOf(entry.get("type"));
            String value = String.valueOf(entry.get("value"));

            AscensionRequirement req = requirementFactory.create(type, value);
            if (req != null) {
                requirements.add(req);
            } else {
                plugin.getLogger().warning("Invalid requirement type: " + type);
            }
        }
        plugin.getLogger().info("Loaded " + requirements.size() + " ascension requirements.");
        }

    public List<AscensionRequirement> getRequirements() {
        return this.requirements;
    }

    public boolean canAscend(Player player) {
        return this.requirements.stream().allMatch(req -> req.isMet(player));
    }

    public void performAscension(Player player) {
        if (!canAscend(player)) {
            return;
        }

        if (config.getBoolean("ascension.reset.rank", true)) {
            plugin.getPrisonAPI().getRanksApi().resetPlayerRank(player);
        }

        if (config.getBoolean("ascension.reset.prestige", true)) {
            plugin.getPrisonAPI().getPrestigesApi().setPlayerPrestige(player, 0);
        }

        if (config.getBoolean("ascension.reset.balance", true)) {
            plugin.getPrisonAPI().getCurrencyApi().setBalance(player, "money", 0);
        }

        plugin.getAscensionLevelManager().incrementLevel(player.getUniqueId());
    }

    public void reloadRequirements() {
        plugin.reloadConfig();
        loadRequirements();
    }

}
