package dev.phoenixxo.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ResourceConfig {
    private final JavaPlugin plugin;
    private final String jarPath;          // e.g., "perks.yml"
    private final String dataPath;         // e.g., "perks.yml"
    private final int expectVersion;       // from "config-version" in YAML (optional)

    private File file;
    private FileConfiguration cfg;

    public ResourceConfig(JavaPlugin plugin, String jarPath, String dataPath, int expectVersion) {
        this.plugin = plugin;
        this.jarPath = jarPath;
        this.dataPath = dataPath;
        this.expectVersion = expectVersion;
    }

    /** Ensure exists + load; if version mismatch, backup old and log. */
    public void load() {
        ensureExists();
        reload();
        checkVersionAndWarn();
    }

    /** Re-reads from disk. */
    public void reload() {
        this.file = new File(plugin.getDataFolder(), dataPath);
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    /** Saves current in-memory config to disk (if you modified it at runtime). */
    public void save() {
        if (cfg == null || file == null) return;
        try {
            cfg.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[BetterRebirth] Failed to save " + dataPath + ": " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() { return cfg; }
    public File getFile() { return file; }

    // ------------------ internals ------------------

    private void ensureExists() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            Bukkit.getLogger().warning("[BetterRebirth] Could not create plugin data folder.");
        }
        File f = new File(plugin.getDataFolder(), dataPath);
        if (!f.exists()) {
            // copy from jar
            try (InputStream in = plugin.getResource(jarPath)) {
                if (in == null) {
                    // No bundled resource—create empty file
                    Files.createDirectories(f.getParentFile().toPath());
                    if (f.createNewFile()) {
                        Bukkit.getLogger().info("[BetterRebirth] Created empty " + dataPath);
                    }
                } else {
                    Files.createDirectories(f.getParentFile().toPath());
                    try (OutputStream out = new FileOutputStream(f)) {
                        in.transferTo(out);
                    }
                    Bukkit.getLogger().info("[BetterRebirth] Installed default " + dataPath + " from jar");
                }
            } catch (IOException e) {
                Bukkit.getLogger().severe("[BetterRebirth] Failed to install " + dataPath + ": " + e.getMessage());
            }
        }
        this.file = f;
    }

    private void checkVersionAndWarn() {
        if (expectVersion <= 0 || cfg == null) return;
        int have = cfg.getInt("config-version", -1);
        if (have == -1) {
            Bukkit.getLogger().warning("[BetterRebirth] " + dataPath + " missing 'config-version'. Consider updating.");
            return;
        }
        if (have < expectVersion) {
            // backup old file
            String stamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            File backup = new File(file.getParentFile(), file.getName() + ".backup-" + have + "-" + stamp + ".yml");
            try {
                Files.copy(file.toPath(), backup.toPath());
                Bukkit.getLogger().warning("[BetterRebirth] " + dataPath + " is outdated (have " + have + ", expect "
                        + expectVersion + "). Backed up to " + backup.getName() + ". Consider merging changes.");
            } catch (IOException e) {
                Bukkit.getLogger().severe("[BetterRebirth] Failed to backup " + dataPath + ": " + e.getMessage());
            }
        }
    }
}
