package dev.phoenixxo.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginLogger {

    public static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";

    private final Logger logger;
    private final String pluginName;

    public PluginLogger(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
        this.pluginName = plugin.getDescription().getName();
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void warning(String msg) {
        logger.warning(msg);
    }

    public void severe(String msg) {
        logger.severe(RED + msg + RESET);
    }

    public void log(Level level, String msg) {
        logger.log(level, msg);
    }
}

