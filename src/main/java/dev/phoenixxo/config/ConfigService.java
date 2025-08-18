package dev.phoenixxo.config;

import dev.phoenixxo.BetterRebirth;

import java.util.*;


/**
 * The ConfigService class is responsible for managing and handling YAML configuration
 * files for a plugin. It provides methods to register, load, reload, and access
 * configuration files from both the file system and the plugin's JAR file.
 */
public class ConfigService {

    public final BetterRebirth plugin = BetterRebirth.getInstance();
    private final Map<String, ResourceConfig> files = new HashMap<>();

    /**
     * Register and load a YAML resource.
     * @param key logical name (e.g., "perks", "messages")
     * @param resourceOnClasspath path inside jar (e.g., "perks.yml")
     * @param targetFileName path under data folder (e.g., "perks.yml")
     * @param expectVersion config-version value expected (<=0 disables version check)
     */
    public ResourceConfig register(String key, String resourceOnClasspath, String targetFileName, int expectVersion) {
        ResourceConfig rc = new ResourceConfig(plugin, resourceOnClasspath, targetFileName, expectVersion);
        rc.load(); // ensure exists + load + optional version backup/log
        files.put(key, rc);
        return rc;
    }

    public ResourceConfig get(String key) { return files.get(key); }

    public Map<String, ResourceConfig> all() { return Collections.unmodifiableMap(files); }

    /** Reload all registered YAMLs from disk. */
    public void reloadAll() {
        for (ResourceConfig rc : files.values()) rc.reload();
    }
}
