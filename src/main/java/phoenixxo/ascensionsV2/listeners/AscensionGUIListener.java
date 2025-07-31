package phoenixxo.ascensionsV2.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import phoenixxo.ascensionsV2.AscensionsV2;
import phoenixxo.ascensionsV2.util.AscensionGUIHolder;
import phoenixxo.ascensionsV2.util.MessageUtil;

public class AscensionGUIListener implements Listener {

    private final AscensionsV2 plugin;

    public AscensionGUIListener(AscensionsV2 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!(event.getInventory().getHolder() instanceof AscensionGUIHolder)) return;

        event.setCancelled(true);

        if (event.getSlot() == 13) {
            if (plugin.getAscensionManager().canAscend(player)) {
                plugin.getAscensionManager().performAscension(player);
                player.closeInventory();
                player.sendMessage(MessageUtil.get("ascension.messages.success"));
            } else {
                player.sendMessage(MessageUtil.get("ascension.messages.fail"));
                player.closeInventory();
            }
        }
    }

}
