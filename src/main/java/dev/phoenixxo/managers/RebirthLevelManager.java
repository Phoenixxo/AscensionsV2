package dev.phoenixxo.managers;

import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.util.DataService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class RebirthLevelManager {

    private static final String ROOT = "players";
    private static final String KEY = "rebirth";

    private final BetterRebirth plugin = BetterRebirth.getInstance();
    private final DataService dataService = plugin.getDataService();

   private FileConfiguration cfg() {
        return dataService.getConfig();
   }

   private ConfigurationSection playerSection(UUID uuid) {
       ConfigurationSection players = dataService.getOrCreateSection(ROOT);
       ConfigurationSection player = players.getConfigurationSection(uuid.toString());
       if (player == null) player = players.createSection(uuid.toString());
       return player;
   }

    public int getRebirthLevel(UUID playerID) {
        return cfg().getInt(ROOT + "." + playerID + "." + KEY, 0);
    }

    public void setRebirthLevel (UUID playerID, int level) {
        playerSection(playerID).set(KEY, level);
        dataService.markDirtyAndAutosave();
    }

    public int incrementLevel(UUID playerID) {
        int next = getRebirthLevel(playerID) + 1;
        setRebirthLevel(playerID, next);
        return next;
    }

}
