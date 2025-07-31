package phoenixxo.ascensionsV2.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import phoenixxo.ascensionsV2.AscensionsV2;
import phoenixxo.ascensionsV2.requirements.AscensionRequirement;
import phoenixxo.ascensionsV2.util.AscensionGUIHolder;


import java.util.*;

import static me.clip.placeholderapi.libs.kyori.adventure.text.format.TextColor.color;

public class ascensionGUI {

    private AscensionsV2 plugin;

    public ascensionGUI(AscensionsV2 plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(new AscensionGUIHolder(), 27, Component.text("Ascension Status", NamedTextColor.DARK_PURPLE));

        List<AscensionRequirement> reqs = this.plugin.getAscensionManager().getRequirements();

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
        boolean allMet = plugin.getAscensionManager().canAscend(player);
        TextColor color = allMet ? NamedTextColor.GREEN : NamedTextColor.RED;

        ItemStack item = new ItemStack(allMet ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Display name
            Component title = Component.text((allMet ? "✓ " : "✗ ") + "Ascension Ready")
                    .color(color)
                    .decoration(TextDecoration.BOLD, true);
            meta.displayName(title);

            // Lore lines
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("--------------------", NamedTextColor.GRAY));

            for (AscensionRequirement requirement : plugin.getAscensionManager().getRequirements()) {
                boolean met = requirement.isMet(player);
                TextColor lineColor = met ? NamedTextColor.GREEN : NamedTextColor.RED;

                Component line = Component.text(met ? "✓ " : "✗ ", lineColor)
                        .append(requirement.getDisplay(player).colorIfAbsent(lineColor));

                lore.add(line);
            }

            meta.lore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createAscendButton (Player player) {
        boolean canAscend = plugin.getAscensionManager().canAscend(player);
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
