package dev.phoenixxo.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataService {
    private final Plugin plugin;
    private final File filePath;
    private FileConfiguration config;

    // Write coordination
    private final AtomicBoolean dirty = new AtomicBoolean(false);
    private final AtomicBoolean saving = new AtomicBoolean(false);
    private int scheduledTaskId = -1;

    // Time to wait before writing again
    private final long debounceMs = 1500;

    public DataService(Plugin plugin, String filename) {
        this.plugin = plugin;
        this.filePath = new File(plugin.getDataFolder(), filename);

        try {
            plugin.getDataFolder().mkdirs();
            if (!filePath.exists()) filePath.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create " + filename, e);
        }

        this.config = YamlConfiguration.loadConfiguration(filePath);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public ConfigurationSection getOrCreateSection(String path) {
        ConfigurationSection sec = config.getConfigurationSection(path);

        if (sec == null) sec = config.createSection(path);
        return sec;
    }

    /** Mark the document dirty; will auto-save after debounce. */
    public void markDirtyAndAutosave() {
        dirty.set(true);
        scheduleDebounceSave();
    }

    /** Save immediately on main thread or dispatch async. */
    public void saveAsync() {
        if (saving.getAndSet(true)) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                synchronized (config) {
                    config.save(filePath);
                }
                dirty.set(false);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save " + filePath.getName() + ": " + e.getMessage());
            } finally {
                saving.set(false);
            }
        });
    }

    /** Force a blocking save */
    public void saveSync() {
        try {
            synchronized (config) {
                config.save(filePath);
            }
            dirty.set(false);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save " + filePath.getName() + ": " + e.getMessage());
        }
    }

    /** Called on plugin disable to flush pending writes.*/
    public void shutdown() {
        if (scheduledTaskId != -1) {
            Bukkit.getScheduler().cancelTask(scheduledTaskId);
            scheduledTaskId = -1;
        }

        if (dirty.get()) saveSync();
    }

    private void scheduleDebounceSave() {
        if (scheduledTaskId != -1) {
            Bukkit.getScheduler().cancelTask(scheduledTaskId);
            scheduledTaskId = -1;
        }
        scheduledTaskId = Bukkit.getScheduler().runTaskLater(plugin, this::saveAsync, ticks(debounceMs)).getTaskId();
    }

    public synchronized void reload() {
        if (scheduledTaskId != -1) {
            Bukkit.getScheduler().cancelTask(scheduledTaskId);
            scheduledTaskId = -1;
        }
        if (dirty.get()) {
            saveSync();
        }

        this.config = YamlConfiguration.loadConfiguration(filePath);

        dirty.set(false);
        saving.set(false);
    }

    private long ticks(long ms) {
        // 20 ticks / sec
        return Math.max(1, ms * 20 / 1000);
    }
}
