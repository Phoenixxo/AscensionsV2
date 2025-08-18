package dev.phoenixxo.data;

import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.util.DataService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

/**
 * Stores per-player:
 * - ranks: nodeId -> rank (int >= 0)
 * - spentPoints: total points spent
 *
 * Layout:
 * players.[uuid].perks.spent: int
 * players.[uuid].perks.ranks.[nodeId]: int
 */
public class PlayerDataManager {

    private final BetterRebirth plugin = BetterRebirth.getInstance();
    private final DataService dataService = plugin.getDataService();;

    // in memory cache
    private final Map<UUID, PlayerPerkData> cache = new HashMap<>();

    private static final String ROOT = "players";
    private static final String NAMESPACE = "perks";

    public PlayerPerkData get(UUID uuid) {
        return cache.computeIfAbsent(uuid, this::loadFromFile);
    }

    private PlayerPerkData loadFromFile(UUID uuid) {
        FileConfiguration config = dataService.getConfig();
        String base = ROOT + "." + uuid + "." + NAMESPACE;
        ConfigurationSection perks = config.getConfigurationSection(base);


        if (perks != null) {
            int spent = perks.getInt("spent", 0);
            Map<String, Integer> ranks = new HashMap<>();
            ConfigurationSection rankSec = perks.getConfigurationSection("ranks");
            if (rankSec != null) {
                for (String nodeId : rankSec.getKeys(false)) {
                    ranks.put(nodeId, Math.max(0, rankSec.getInt(nodeId, 0)));
                }

            }
            return new PlayerPerkData(spent, ranks);
        }
        return new PlayerPerkData(0, new HashMap<>()); // empty default value
    }

    public void save(UUID uuid) {
        PlayerPerkData data = cache.get(uuid);

        if (data == null) return;

        FileConfiguration cfg = dataService.getConfig();

        ConfigurationSection players = cfg.getConfigurationSection(ROOT);
        if (players == null) players = cfg.createSection(ROOT);

        ConfigurationSection playerSec = players.getConfigurationSection(uuid.toString());
        if (playerSec == null) playerSec = players.createSection(uuid.toString());

        ConfigurationSection perksSec = playerSec.getConfigurationSection(NAMESPACE);
        if (perksSec == null) perksSec = playerSec.createSection(NAMESPACE);

        perksSec.set("spent", Math.max(0, data.getSpentPoints()));

        ConfigurationSection rankSec = perksSec.getConfigurationSection("ranks");
        if (rankSec == null) rankSec = perksSec.createSection("ranks");

        for (String key : new HashSet<>(rankSec.getKeys(false))) {
            rankSec.set(key, null);
        }

        for (Map.Entry<String, Integer> e : data.getRanks().entrySet()) {
            rankSec.set(e.getKey(), Math.max(0, e.getValue()));
        }

        dataService.markDirtyAndAutosave();
    }

    public void saveAll() {
        for (UUID uuid: cache.keySet()) save(uuid);
    }

    public void unload(UUID uuid) { cache.remove(uuid); }

    public void reload() {
        saveAll();
        cache.clear();
        for (UUID uuid: getAllKnownPlayers()) {
            cache.put(uuid, loadFromFile(uuid));
        }
    }

    private Set<UUID> getAllKnownPlayers() {
        Set<UUID> players = new HashSet<>();
        ConfigurationSection playersSec = dataService.getConfig().getConfigurationSection(ROOT);
        if (playersSec != null) {
            for (String key : playersSec.getKeys(false)) {
                try {
                    players.add(UUID.fromString(key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return players;
    }

}
