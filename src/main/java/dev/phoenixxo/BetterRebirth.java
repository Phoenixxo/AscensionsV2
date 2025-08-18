package dev.phoenixxo;

import dev.drawethree.xprison.api.XPrisonAPI;
import dev.phoenixxo.commands.RebirthCommand;
import dev.phoenixxo.commands.RebirthTabCompleter;
import dev.phoenixxo.config.ConfigService;
import dev.phoenixxo.config.ResourceConfig;
import dev.phoenixxo.data.PlayerDataManager;
import dev.phoenixxo.listeners.PerkClickHandler;
import dev.phoenixxo.listeners.RebirthGUIListener;
import dev.phoenixxo.managers.*;
import dev.phoenixxo.menus.PerkTreeGUI;
import dev.phoenixxo.menus.rebirthGUI;
import dev.phoenixxo.perks.RebirthPerks;
import dev.phoenixxo.perks.api.RebirthPerksAPI;
import dev.phoenixxo.requirements.RequirementFactory;
import dev.phoenixxo.util.RebirthExpansion;
import dev.phoenixxo.util.DataService;
import dev.phoenixxo.util.MessageUtil;
import dev.phoenixxo.util.PluginLogger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class BetterRebirth extends JavaPlugin {
    @Getter
    private static BetterRebirth instance;

    private PluginLogger logger;

    @Getter
    private XPrisonAPI prisonAPI;
    @Getter
    private RebirthLevelManager rebirthLevelManager;
    @Getter
    @Setter
    private RequirementFactory requirementFactory;
    @Getter
    private RebirthManager rebirthManager;
    @Getter
    private rebirthGUI rebirthGUI;
    @Getter
    private PerkTreeGUI perkTreeGUI;
    @Getter
    private MessagesManager messagesManager;
    @Getter
    private PrefixManager prefixManager;
    @Getter
    private PlayerDataManager playerDataManager;
    @Getter
    private DataService dataService;
    @Getter
    private RebirthPerks rebirthPerks;
    private ConfigService configs;


    @Override
    public void onEnable() {
        this.logger = new PluginLogger(this);
        logger.info("Starting BetterRebirth...");
        saveDefaultConfig();
        instance = this;
        configs = new ConfigService();
        messagesManager = new MessagesManager(this);
        MessageUtil.init(this);
        this.dataService = new DataService(this, "data.yml");



        // Check if X-Prison plugin is enabled
        Bukkit.getScheduler().runTask(this, () -> {
            if (getServer().getPluginManager().isPluginEnabled("X-Prison")) {
                try {
                    this.prisonAPI = XPrisonAPI.getInstance();

                    // configs
                    ResourceConfig perks = this.configs.register("perks","perks.yml","perks.yml", 1);
                    ResourceConfig messages = this.configs.register("messages", "messages.yml", "messages.yml", 1);
                    ResourceConfig prefixes = this.configs.register("prefixes", "prefixes.yml", "prefixes.yml", 1);

                    // Managers
                    this.rebirthLevelManager = new RebirthLevelManager();
                    this.requirementFactory = new RequirementFactory(prisonAPI);
                    this.rebirthManager = new RebirthManager(this);
                    this.prefixManager = new PrefixManager(this);
                    this.playerDataManager = new PlayerDataManager();
                    this.rebirthManager.loadRequirements();

                    // Perks
                    this.rebirthPerks = new RebirthPerks();
                    this.rebirthPerks.load(perks.getConfig());
                    logger.info("Loaded perk nodes: " + this.rebirthPerks.getApi().getNodes().size());

                    // GUI & Listeners
                    this.rebirthGUI = new rebirthGUI(this);
                    this.perkTreeGUI = new PerkTreeGUI();
                    this.getServer().getPluginManager().registerEvents(new PerkClickHandler(this.perkTreeGUI), this);
                    this.getServer().getPluginManager().registerEvents(new RebirthGUIListener(this), this);

                    // Commands
                    this.getCommand("rebirth").setExecutor(new RebirthCommand(this));
                    this.getCommand("rebirth").setTabCompleter(new RebirthTabCompleter());
                    logger.info("Successfully hooked into X-Prison API");
                } catch (Exception e) {
                    logger.severe(e.getLocalizedMessage());
                    getServer().getPluginManager().disablePlugin(this);
                }
            } else {
                logger.severe("X-Prison not found, disabling.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                new RebirthExpansion(this).register();
                logger.info("PlaceholderAPI expansion registered.");
            } else {
                logger.warning("PlaceholderAPI not found.");
            }
        });

    }

    @Override
    public void onDisable() {
            long start = System.currentTimeMillis();
        getLogger().info("Shutting down…");

            try {

                // Flush gameplay state to in-memory config
                try {
                    if (null != playerDataManager) {
                        this.playerDataManager.saveAll();  // writes to DataService config + marks dirty
                    }
                    if (null != rebirthLevelManager) {
                        dataService.saveSync();
                    }
                    if (null != this.rebirthPerks) {
                        rebirthPerks.save();
                    }
                } catch (Exception ex) {
                    getLogger().severe("Failed saving managers before shutdown: " + ex.getMessage());
                    ex.printStackTrace();
                }

                // Cancel scheduled tasks (prevents late writes after save)
                getServer().getScheduler().cancelTasks(this);

                // Flush data.yml to disk
                if (null != dataService) {
                    this.dataService.shutdown();  // cancels debounced task, saveSync() if dirty
                }

                getLogger().info("Shutdown complete in " +
                        (System.currentTimeMillis() - start) + " ms.");
            } catch (Throwable t) {
                // never throw from onDisable; just log
                getLogger().severe("onDisable error: " + t.getMessage());
                t.printStackTrace();
        }

    }

    public RebirthPerksAPI getPerksAPI() { return rebirthPerks.getApi(); }
}
