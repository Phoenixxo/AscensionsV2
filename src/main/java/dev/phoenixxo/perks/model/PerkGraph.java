package dev.phoenixxo.perks.model;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class PerkGraph {
    private final Map<String, PerkNode> nodes = new LinkedHashMap<>();

    /**
     *
     * @param strict If true, throws IllegalStateException, otherwise just gives a warning.
     */
    void validateAcyclic(boolean strict) {
        Map<String, Integer> state = new HashMap<>();
        Deque<String> stack = new ArrayDeque<>();
        List<List<String>> cycles = new ArrayList<>();

        for (String id : nodes.keySet()) {
            if (state.getOrDefault(id, 0) == 0) {
                dfsCycle(id, state, stack, cycles);
            }
        }

        if (!cycles.isEmpty()) {
            StringBuilder sb = new StringBuilder("Detected cycles(s) in perks.yml: ");
            int i = 0;
            for (List<String> cyc : cycles) {
                if (i++ > 0) sb.append(" | ");
                sb.append(String.join(" -> ", cyc)).append(" -> ").append(cyc.get(0));
            }
            if (strict) {
                throw new IllegalStateException(sb.toString());
            } else {
                Bukkit.getLogger().warning(sb.toString());
            }
        }
    }

    private void dfsCycle(String id, Map<String, Integer> state, Deque<String> stack, List<List<String>> cycles) {
        state.put(id, 1);
        stack.push(id);

        PerkNode n = nodes.get(id);
        if (n != null) {
            for (String dep : n.getRequires()) {
                if (!nodes.containsKey(dep)) continue;
                int st = state.getOrDefault(dep, 0);
                if (st == 0) {
                    dfsCycle(dep, state, stack, cycles);
                } else if (st == 1) {
                    // Found a cycle. Extract path to the dep.
                    List<String> cyc = new ArrayList<>();
                    for (String s : stack) {
                        cyc.add(s);
                        if (s.equals(dep)) break;
                    }
                    Collections.reverse(cyc);
                    cycles.add(cyc);
                }
            }
        }

        stack.pop();
        state.put(id, 2);
    }

    public void loadFromConfig(FileConfiguration config) {
        nodes.clear();
        ConfigurationSection root = config.getConfigurationSection("perks.perks_config");
        if (root == null) return;
        for (String id: root.getKeys(false)) {
            ConfigurationSection s = root.getConfigurationSection(id);
            if (s == null) continue;
            PerkNode n = new PerkNode(
                    id,
                    s.getString("name", id),
                    s.getString("icon", Material.BOOK.name()),
                    s.getString("category", "general"),
                    s.getInt("slot", 0),
                    s.getInt("max_rank", 1),
                    s.getInt("cost_per_rank", 1),
                    s.getString("bonus.type", "percentage"),
                    s.getDouble("bonus.base", 0.0),
                    s.getDouble("bonus.per_rank", 0.0),
                    s.getDouble("bonus.max", Double.MAX_VALUE),
                    s.getStringList("requires"),
                    s.getStringList("neighbors")
            );
            nodes.put(id, n);
        }
        boolean strict = config.getBoolean("perks.strict_graph_validation");
        validateAcyclic(strict);
    }

    public PerkNode get(String id) {
        return nodes.get(id);
    }

    public Collection<PerkNode> all() { return nodes.values(); }

    boolean prerequisitesMet(PlayerPerkState ps, String nodeId) {
        PerkNode n = nodes.get(nodeId);
        if (n == null) return false;
        for (String req : n.getRequires()) {
            if (ps.rankOf(req) <= 0) return false;
        }
        return true;
    }

    public boolean canRankUp(PlayerPerkState ps, String nodeId, int pointsAvailable) {
        PerkNode n = nodes.get(nodeId);
        if (n == null) return false;
        int cur = ps.rankOf(nodeId);
        if ( cur >= n.getMaxRank() ) return false;
        if (!prerequisitesMet(ps, nodeId)) return false;
        return pointsAvailable >= n.getCostPerRank();
    }
}
