package phoenixxo.ascensionsV2.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import phoenixxo.ascensionsV2.AscensionsV2;
import phoenixxo.ascensionsV2.util.MessageUtil;

import java.io.File;
import java.io.IOException;

public class AscensionMessagesManager {

    private final AscensionsV2 plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public AscensionMessagesManager(AscensionsV2 plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getMessagesConfig() {
        return this.messagesConfig;
    }

    public void saveMessagesConfig() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save messages.yml: " + e.getLocalizedMessage());
        }
    }

    public void reload() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
}
