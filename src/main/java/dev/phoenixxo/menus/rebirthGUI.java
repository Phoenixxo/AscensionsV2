package dev.phoenixxo.menus;

import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.requirements.RebirthRequirement;
import dev.phoenixxo.util.RebirthGUIHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class rebirthGUI {

    private BetterRebirth plugin;

    public rebirthGUI(BetterRebirth plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(new RebirthGUIHolder(), 27, Component.text("Ascension Status", NamedTextColor.DARK_PURPLE));

        List<RebirthRequirement> reqs = this.plugin.getRebirthManager().getRequirements();

        gui.setItem(11, createRequirementItem(player));
        gui.setItem(13, createAscendButton(player));
        gui.setItem(15, new ItemStack(Material.BEDROCK));

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();

        if (fillerMeta != null) {
            fillerMeta.displayName(Component.empty());
            filler.setItemMeta(fillerMeta);
        }

        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) != null) {
                continue;
            }
            gui.setItem(i, filler);
        }

        player.openInventory(gui);
    }

    private ItemStack createRequirementItem(Player player) {
        boolean allMet = plugin.getRebirthManager().canRebirth(player);
        TextColor color = allMet ? NamedTextColor.GREEN : NamedTextColor.RED;

        ItemStack item = new ItemStack(allMet ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Display Name
            String prefix = allMet ? "<green>✓ " : "<red>✗ ";
            Component title = MiniMessage.miniMessage().deserialize(prefix + "<bold> Ascension Ready </bold>");
            meta.displayName(title);

            // Lore lines
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("--------------------", NamedTextColor.GRAY));

            plugin.getRebirthManager().getRequirements().forEach(ascensionRequirement -> {
                boolean met = ascensionRequirement.isMet(player);
                String check = met ? "<green>✓ " : "<red>✗ ";
                Component checkComponent = MiniMessage.miniMessage().deserialize(check);

                Component line = checkComponent.append(ascensionRequirement.getDisplay(player));
                lore.add(line);
            });

            meta.lore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createAscendButton (Player player) {
        boolean canAscend = plugin.getRebirthManager().canRebirth(player);
        ItemStack item = new ItemStack(canAscend ? Material.NETHER_STAR : Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component title = Component.text(canAscend ? "Click to Ascend!" : "Requirements Not Met", NamedTextColor.GOLD);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Reset your progress and rise again.", NamedTextColor.GRAY));
            if (!canAscend) {
                lore.add(Component.text("You do not meet all the requirements.", NamedTextColor.RED));
            }

            meta.displayName(title);
            meta.lore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
}
