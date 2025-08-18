package dev.phoenixxo.listeners;

import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.menus.PerkTreeGUI;
import dev.phoenixxo.perks.api.RebirthPerksAPI;
import dev.phoenixxo.perks.model.PerkNode;
import dev.phoenixxo.util.MessageUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

/**
 * Handles clicks within the PerkTreeGUI.
 * <p>
 * This listener:
 * <ul>
 *   <li>Ensures the click originated from the perk tree menu.</li>
 *   <li>Cancels the event to prevent item movement.</li>
 *   <li>Reads the clicked item's node identifier from its PersistentDataContainer.</li>
 *   <li>Attempts to rank up the corresponding perk via the RebirthPerksAPI.</li>
 *   <li>Provides user feedback and re-opens the GUI when appropriate.</li>
 * </ul>
 */
public class PerkClickHandler implements Listener {

    /**
     * Reference to the main plugin instance used to access shared services.
     */
    private final BetterRebirth plugin = BetterRebirth.getInstance();

    /**
     * GUI instance used for re-opening the menu after successful upgrades.
     */
    private PerkTreeGUI perkGUI;

    /**
     * Persistent data key used to store and retrieve the perk node identifier on menu items.
     */
    private final NamespacedKey KEY_NODE_ID = new NamespacedKey(plugin, "rebirth_node_id");

    /**
     * Cached reference to the perks API for rank-up and lookup operations.
     */
    private RebirthPerksAPI perksAPI;

    /**
     * Creates a new click handler bound to the provided PerkTreeGUI.
     *
     * @param perkGUI the perk tree GUI instance this handler is managing interactions for
     */
    public PerkClickHandler(PerkTreeGUI perkGUI) {
        this.perkGUI = perkGUI;
        this.perksAPI = this.plugin.getPerksAPI();
    }

    /**
     * Handles inventory click events originating from the PerkTreeGUI.
     * <p>
     * Behavior:
     * <ul>
     *   <li>Returns early if the click did not come from the perk menu or the clicker is not a player.</li>
     *   <li>Cancels the event to prevent default inventory behavior.</li>
     *   <li>Extracts a node ID from the clicked item's PDC and attempts to rank up that perk.</li>
     *   <li>On success, sends a confirmation message and re-opens the GUI.</li>
     *   <li>On failure, provides a specific reason when possible (maxed, prerequisites, points).</li>
     * </ul>
     *
     * @param e the inventory click event fired by Bukkit
     */
    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        final Inventory top = e.getView().getTopInventory();
        if (!PerkTreeGUI.isMenu(top)) return;

        e.setCancelled(true);

        if (e.getRawSlot() < 0 || e.getRawSlot() >= top.getSize()) return;

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String nodeId = clicked.getItemMeta().getPersistentDataContainer().get(KEY_NODE_ID, PersistentDataType.STRING);
        if (nodeId == null || nodeId.isEmpty()) return;

        boolean ok = perksAPI.tryRankUp(p, nodeId);

        if (ok) {
            p.sendMessage(MessageUtil.msgUpgraded(nodeId));
            plugin.getPlayerDataManager().save(p.getUniqueId());
            this.perkGUI.open(p);
        } else {
            PerkNode n = this.perksAPI.getNode(nodeId);
            if (n == null) {
                p.sendMessage(MessageUtil.msgUnknownNode());
                return;
            }

            int pts = this.perksAPI.pointsAvailable(p);

            boolean prereqs = prerequisitesMet(p, n);
            boolean maxed = this.perksAPI.rankOf(p, n.getId()) >= n.getMaxRank();
            if (maxed) p.sendMessage(MessageUtil.msgAlreadyMax());
            else if (!prereqs) p.sendMessage(MessageUtil.msgMissingRequirements());
            else if (pts < n.getCostPerRank()) p.sendMessage(MessageUtil.msgNotEnoughPoints());
            else p.sendMessage(MessageUtil.msgCannotUpgrade());
        }
    }

    /**
     * Verifies whether all prerequisite nodes for the given node are currently active for the player.
     * <p>
     * This uses the API's active values as a proxy for "rank > 0" on required nodes.
     *
     * @param p the player attempting the upgrade
     * @param n the node being upgraded
     * @return true if all prerequisites are active; false otherwise
     */
    private boolean prerequisitesMet(Player p, PerkNode n) {
        // Basic check using ranks known through API values
        Map<String, Double> active = this.perksAPI.allActiveValues(p);
        for (String req : n.getRequires()) {
            // req must have rank >0; API gives values only for rank>0
            if (!active.containsKey(req)) return false;
        }
        return true;
    }
}
