package dev.phoenixxo.listeners;

import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.util.RebirthGUIHolder;
import dev.phoenixxo.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RebirthGUIListener implements Listener {

    private final BetterRebirth plugin;

    public RebirthGUIListener(BetterRebirth plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!(event.getInventory().getHolder() instanceof RebirthGUIHolder)) return;

        event.setCancelled(true);

        if (event.getSlot() == 13) {
            if (plugin.getRebirthManager().canRebirth(player)) {
                plugin.getRebirthManager().performRebirth(player);
                player.closeInventory();
                player.sendMessage(MessageUtil.get("rebirth.messages.success"));
            } else {
                player.sendMessage(MessageUtil.get("rebirth.messages.fail"));
                player.closeInventory();
            }
        }
    }

}
