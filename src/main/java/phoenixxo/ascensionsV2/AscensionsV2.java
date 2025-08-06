package phoenixxo.ascensionsV2;

import dev.drawethree.xprison.api.XPrisonAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import phoenixxo.ascensionsV2.commands.AscensionsCommand;
import phoenixxo.ascensionsV2.commands.AscensionsTabCompleter;
import phoenixxo.ascensionsV2.listeners.AscensionGUIListener;
import phoenixxo.ascensionsV2.managers.AscensionLevelManager;
import phoenixxo.ascensionsV2.managers.AscensionManager;
import phoenixxo.ascensionsV2.managers.AscensionMessagesManager;
import phoenixxo.ascensionsV2.managers.PrefixManager;
import phoenixxo.ascensionsV2.menus.ascensionGUI;
import phoenixxo.ascensionsV2.requirements.RequirementFactory;
import phoenixxo.ascensionsV2.util.AscensionExpansion;
import phoenixxo.ascensionsV2.util.MessageUtil;
import phoenixxo.ascensionsV2.util.PluginLogger;


public class AscensionsV2 extends JavaPlugin {
    private static AscensionsV2 instance;

    private PluginLogger logger;

    private XPrisonAPI prisonAPI;
    private AscensionLevelManager ascensionLevelManager;
    private RequirementFactory requirementFactory;
    private AscensionManager ascensionManager;
    private ascensionGUI ascensionGUI;
    private AscensionMessagesManager messagesManager;
    private PrefixManager prefixManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        instance = this;

        this.logger = new PluginLogger(this);
        this.messagesManager = new AscensionMessagesManager(this);
        MessageUtil.init(this);
        logger.info("Starting AscensionsV2...");

        // Check if X-Prison plugin is enabled
        Bukkit.getScheduler().runTask(this, () -> {
            if (getServer().getPluginManager().isPluginEnabled("X-Prison")) {
                try {
                    prisonAPI = XPrisonAPI.getInstance();

                    // Managers
                    this.ascensionLevelManager = new AscensionLevelManager(this);
                    this.requirementFactory = new RequirementFactory(prisonAPI);
                    this.ascensionManager = new AscensionManager(this);
                    this.prefixManager = new PrefixManager(this);

                    this.ascensionManager.loadRequirements();

                    // GUI & Listeners
                    this.ascensionGUI = new ascensionGUI(this);
                    this.getServer().getPluginManager().registerEvents(new AscensionGUIListener(this), this);

                    // Commands
                    this.getCommand("ascensions").setExecutor(new AscensionsCommand(this));
                    this.getCommand("ascensions").setTabCompleter(new AscensionsTabCompleter());
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
                new AscensionExpansion(this).register();
                logger.info("PlaceholderAPI expansion registered.");
            } else {
                logger.warning("PlaceholderAPI not found.");
            }
        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("Shutting down.");
    }

    public static AscensionsV2 getInstance() {
        return instance;
    }

    public XPrisonAPI getPrisonAPI() {
        return this.prisonAPI;
    }

    public AscensionLevelManager getAscensionLevelManager() {
        return this.ascensionLevelManager;
    }

    public RequirementFactory getRequirementFactory() {
        return this.requirementFactory;
    }

    public void setRequirementFactory (RequirementFactory factory) {
        this.requirementFactory = factory;
    }

    public AscensionManager getAscensionManager() {
        return this.ascensionManager;
    }

    public ascensionGUI getAscensionGUI() {
        return this.ascensionGUI;
    }

    public AscensionMessagesManager getMessagesManager() {
        return this.messagesManager;
    }

    public PrefixManager getPrefixManager() {
        return this.prefixManager;
    }

}
