package dev.phoenixxo.perks.storage;

import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.perks.model.PlayerPerkState;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StoragePdcYaml implements PerkStorage {
    private final BetterRebirth plugin = BetterRebirth.getInstance();
    private final Map<UUID, PlayerPerkState> cache = new HashMap<>();
    private final NamespacedKey KEY_DATA;

    public StoragePdcYaml() {
        this.KEY_DATA = new NamespacedKey(plugin, "perks");
    }

    @Override
    public void load() {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            loadFromPdc(p);
        }
    }

    @Override
    public void save() {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            persistToPdc(p);
        }

        var dataService = plugin.getDataService();
        var cfg = dataService.getConfig();

        final String ROOT = "players";
        final String NAMESPACE = "perks";

        for (Map.Entry<UUID, PlayerPerkState> entry : cache.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerPerkState ps = entry.getValue();

            String base = ROOT + "." + uuid + "." + NAMESPACE;

            // ensure sections exist
            var playersSec = cfg.getConfigurationSection(ROOT);
            if (playersSec == null) playersSec = cfg.createSection(ROOT);
            var playerSec = playersSec.getConfigurationSection(uuid.toString());
            if (playerSec == null) playerSec = playersSec.createSection(uuid.toString());
            var perksSec = playerSec.getConfigurationSection("perks");
            if (perksSec == null) perksSec = playerSec.createSection("perks");

            // write values
            perksSec.set("spent", ps.getSpentPoints());

            // clear & write ranks
            var ranksSec = perksSec.getConfigurationSection("ranks");
            if (ranksSec == null) ranksSec = perksSec.createSection("ranks");
            for (String old : new java.util.HashSet<>(ranksSec.getKeys(false))) {
                ranksSec.set(old, null);
            }
            for (var e2 : ps.getRanks().entrySet()) {
                ranksSec.set(e2.getKey(), e2.getValue());
            }
        }

        // mark dirty so DataService autosaves
        dataService.markDirtyAndAutosave();
    }

    @Override
    public PlayerPerkState state(UUID uuid) {
        return cache.computeIfAbsent(uuid, PlayerPerkState::new);
    }

    @Override
    public void put(PlayerPerkState state) {
        cache.put(state.getUuid(), state);
    }

    private void loadFromPdc(Player p) {
        PersistentDataContainer pdc = p.getPersistentDataContainer();
        String raw = pdc.get(KEY_DATA, PersistentDataType.STRING);
        if (raw == null || raw.isEmpty()) { return;}
        PlayerPerkState ps = state(p);
        try {
            String[] parts = raw.split(";", 2);
            ps.addSpent(Integer.parseInt(parts[0]));
            if (parts.length > 1 && !parts[1].isEmpty()) {
                String[] pairs = parts[1].split(",");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length == 2) {
                        String node = kv[0];
                        int rank = Integer.parseInt(kv[1]);
                        for (int i = 0; i < rank; i++) {
                            ps.rankUp(node);
                        }
                    }
                }
            }
        } catch (Exception ignored) { }
    }

    private void persistToPdc(Player p) {
        PlayerPerkState ps = state(p);
        StringBuilder sb = new StringBuilder();
        sb.append(ps.getSpentPoints()).append(';');
        boolean first = true;
        for (Map.Entry<String, Integer> e : ps.getRanks().entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append(e.getKey()).append('=').append(e.getValue());
        }
        p.getPersistentDataContainer().set(KEY_DATA, PersistentDataType.STRING, sb.toString());
    }
}
