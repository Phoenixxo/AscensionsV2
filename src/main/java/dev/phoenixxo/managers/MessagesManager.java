package dev.phoenixxo.managers;

import dev.phoenixxo.BetterRebirth;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessagesManager {

    private final BetterRebirth plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public MessagesManager(BetterRebirth plugin) {
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
        saveMessagesConfig();
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
}
