package dev.phoenixxo.perks.api;

import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.perks.model.PerkGraph;
import dev.phoenixxo.perks.model.PerkNode;
import dev.phoenixxo.perks.model.PlayerPerkState;
import dev.phoenixxo.perks.storage.PerkStorage;
import dev.phoenixxo.perks.storage.StoragePdcYaml;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RebirthPerksAPI {
    private final BetterRebirth plugin = BetterRebirth.getInstance();
    private final PerkGraph graph;
    private final PerkStorage storage;

    public RebirthPerksAPI(PerkGraph graph, PerkStorage storage) {
        this.graph = graph;
        this.storage = storage;
    }

    public int totalPoints(Player p) {
        int rebirth = plugin.getRebirthLevelManager().getRebirthLevel(p.getUniqueId());
        File file = new File(plugin.getDataFolder(), "perks.yml");

        int perRebirth = YamlConfiguration.loadConfiguration(file).getInt("perks.points_per_rebirth", 1);
        return rebirth * perRebirth;
    }

    public int pointsAvailable(Player p)  {
        PlayerPerkState ps = storage.state(p);
        return totalPoints(p) - ps.getSpentPoints();
    }

    public boolean tryRankUp(Player p, String nodeId) {
        PlayerPerkState ps = storage.state(p);
        if (!graph.canRankUp(ps, nodeId, pointsAvailable(p))) return false;
        PerkNode n = graph.get(nodeId);
        ps.rankUp(nodeId);
        ps.addSpent(n.getCostPerRank());
        storage.put(ps);
        plugin.getRebirthPerks().save();
        return true;
    }

    public double nodeValue (Player p, String nodeId) {
        PerkNode n = graph.get(nodeId);
        if (n == null) return 0.0;
        int r = storage.state(p).rankOf(nodeId);
        return n.valueForRank(r);
    }

    public Map<String, Double> allActiveValues(Player p) {
        Map<String, Double> out = new HashMap<>();
        PlayerPerkState ps = storage.state(p);
        for (PerkNode n : graph.all()) {
            int r = ps.rankOf(n.getId());
            if (r > 0) out.put(n.getId(), n.valueForRank(r));
        }
        return out;
    }

    public PerkNode getNode(String id) { return graph.get(id); }

    public Collection<PerkNode> getNodes() { return graph.all(); }

    public int rankOf(Player p, String nodeId) {
        PlayerPerkState ps = storage.state(p);
        return ps.rankOf(nodeId);
    }
}
