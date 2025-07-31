package phoenixxo.ascensionsV2.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import phoenixxo.ascensionsV2.AscensionsV2;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AscensionLevelManager {

    private final AscensionsV2 plugin;
    private FileConfiguration data;
    private File file;

    public AscensionLevelManager(AscensionsV2 plugin) {
        this.plugin = plugin;
        loadDataFile();
    }

    public void loadDataFile() {
        this.file = new File(plugin.getDataFolder(), "data.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                plugin.getLogger().info("Created data.yml file.");
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml: " + e.getMessage());
            }
        }

        data = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save data.yml: " + e.getLocalizedMessage());
        }
    }

    public int getAscensionLevel (UUID playerID) {
        return data.getInt("players." + playerID + ".ascension", 0);
    }

    public void setAscensionLevel (UUID playerID, int level) {
        data.set("players." + playerID + ".ascension", level);
        save();
    }

    public void incrementLevel(UUID playerID) {
        int current = getAscensionLevel(playerID);
        setAscensionLevel(playerID, ++current);
    }

    public void reload() {
        data = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getData() {
        return data;
    }
}
