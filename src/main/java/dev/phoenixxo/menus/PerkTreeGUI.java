package dev.phoenixxo.menus;


import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.perks.api.RebirthPerksAPI;
import dev.phoenixxo.perks.model.PerkNode;
import dev.phoenixxo.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PerkTreeGUI {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final BetterRebirth plugin = BetterRebirth.getInstance();
    private final RebirthPerksAPI api;
    private final NamespacedKey KEY_NODE_ID;



    public PerkTreeGUI() {
        api = this.plugin.getPerksAPI();
        KEY_NODE_ID = new NamespacedKey(this.plugin, "rebirth_node_id");
    }


    /** Opens the menu for a player (fresh inventory). */
    public void open(final Player player) {
        final Inventory inv = this.createInventory(player);
        this.renderInto(inv, player);
        player.openInventory(inv);
    }

    /** Repaints an already-opened menu (use after upgrades if you manage title elsewhere). */
    public void refresh(final Player player, final Inventory inv) {
        if (!PerkTreeGUI.isMenu(inv)) return;
        this.renderInto(inv, player);
    }

    /** Builds a new inventory with proper size and chrome (filler/connectors). */
    private Inventory createInventory(final Player player) {
        final FileConfiguration cfg = this.plugin.getConfig();
        final ConfigurationSection gui = cfg.getConfigurationSection("perks.perks_gui");
        final int rows = Math.max(1, Math.min(6, null != gui ? gui.getInt("rows", 6) : 6));
        final int size = rows * 9;

        final Component title = MessageUtil.guiTitle(this.api.pointsAvailable(player));
        final Inventory inv = Bukkit.createInventory(new Holder(), size, title);

        // Filler
        if (null != gui) {
            final ConfigurationSection filler = gui.getConfigurationSection("filler");
            if (null != filler && filler.getBoolean("enabled", true)) {
                final Material mat = PerkTreeGUI.materialOr(filler.getString("material"), Material.BLACK_STAINED_GLASS_PANE);
                final ItemStack pane = PerkTreeGUI.named(mat, PerkTreeGUI.MM.deserialize(MessageUtil.guiFillerNameRaw()), null);
                for (int i = 0; i < size; i++) inv.setItem(i, pane);
            }

            // Optional manual connectors
            final ConfigurationSection cons = gui.getConfigurationSection("connectors");
            if (null != cons && cons.getBoolean("enabled", false)) {
                final Material mat = PerkTreeGUI.materialOr(cons.getString("material"), Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                final ItemStack pane = PerkTreeGUI.named(mat, PerkTreeGUI.MM.deserialize(MessageUtil.guiConnectorsNameRaw()), null);
                for (final int slot : cons.getIntegerList("slots")) {
                    if (0 <= slot && slot < size) inv.setItem(slot, pane);
                }
            }
        }

        return inv;
    }

    /** Places node items according to their configured slot. */
    private void renderInto(final Inventory inv, final Player player) {
        final int points = this.api.pointsAvailable(player);
        final Map<String, Boolean> reqFulfilled = this.buildReqMap(player);

        for (final PerkNode n : this.api.getNodes()) {
            if (0 > n.getSlot() || n.getSlot() >= inv.getSize()) continue;
            final int rank = this.safeRankOf(player, n);
            final boolean atMax = rank >= n.getMaxRank();
            final boolean canAfford = points >= n.getCostPerRank();
            final boolean prereqsOk = n.getRequires().isEmpty() || n.getRequires().stream().allMatch(id -> reqFulfilled.getOrDefault(id, false));

            final State state;
            if (0 < rank) state = State.UNLOCKED;
            else state = (prereqsOk && canAfford) ? State.AVAILABLE : State.LOCKED;

            final ItemStack icon = this.buildNodeItem(player, n, rank, atMax, state, reqFulfilled, points);
            // Tag node id for click handler
            final ItemMeta meta = icon.getItemMeta();
            meta.getPersistentDataContainer().set(this.KEY_NODE_ID, PersistentDataType.STRING, n.getId());
            icon.setItemMeta(meta);

            inv.setItem(n.getSlot(), icon);
        }
    }

    private enum State { UNLOCKED, AVAILABLE, LOCKED }

    private ItemStack buildNodeItem(
            final Player p, final PerkNode n, final int rank, final boolean atMax, final State state,
            final Map<String, Boolean> reqFulfilled, final int points
    ) {
        final Material iconMat = PerkTreeGUI.materialOr(n.getIcon(), Material.BARRIER);
        final ItemStack item = new ItemStack(iconMat);
        final ItemMeta meta = item.getItemMeta();

        // Display name with rank
        meta.displayName(PerkTreeGUI.MM.deserialize("<white>" + n.getName() + "</white> <gray>(</gray><yellow>" + rank + "</yellow><gray>/</gray><yellow>" + n.getMaxRank() + "</yellow><gray>)</gray>"));

        final List<Component> lore = new ArrayList<>(6);
        lore.add(MessageUtil.guiPointsLine(points));
        lore.add(MessageUtil.guiCostPerRank(n.getCostPerRank()));

        final double now = PerkTreeGUI.valueFor(n, Math.max(1, rank)); // show base at 0
        final double next = PerkTreeGUI.valueFor(n, Math.min(n.getMaxRank(), rank + 1));

        lore.add(MessageUtil.guiValueNow(n.getBonusType(), now));
        lore.add(atMax ? MessageUtil.guiUnlockedMax() : MessageUtil.guiValueNext(n.getBonusType(), next));

        if (!n.getRequires().isEmpty()) {
            final List<String> reqLines = n.getRequires().stream()
                    .map(reqId -> MessageUtil.guiRequiresItemRaw(reqFulfilled.getOrDefault(reqId, false), reqId))
                    .collect(Collectors.toList());
            lore.add(MessageUtil.guiRequiresHeader(reqLines));
        }

        switch (state) {
            case UNLOCKED -> {
                if (!atMax) lore.add(MessageUtil.guiUnlockedClickToRankUp());
                else lore.add(MessageUtil.guiUnlockedMax());
            }
            case AVAILABLE -> lore.add(MessageUtil.guiAvailableClick());
            case LOCKED -> lore.add(MessageUtil.guiLocked());
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static double valueFor(final PerkNode n, final int rank) {
        final double v = n.getBase() + n.getPerRank() * (rank - 1);
        return Math.min(v, n.getMax());
    }

    /** If API doesn't expose rank, infer from nodeValue. Prefer api.rankOf if available. */
    private int safeRankOf(final Player p, final PerkNode n) {
        try {
            // If API implements rankOf, use it
            final int r = this.api.rankOf(p, n.getId());
            if (0 <= r) return r;
        } catch (final Throwable ignored) { /* fall back */ }

        final double val = this.api.nodeValue(p, n.getId());
        if (0 >= val) return 0;
        if (0 == n.getPerRank()) return 1;
        final int guess = (int) Math.round(1.0 + (val - n.getBase()) / n.getPerRank());
        return Math.max(1, Math.min(n.getMaxRank(), guess));
    }

    private Map<String, Boolean> buildReqMap(final Player p) {
        // A node is considered "fulfilled" if player has rank > 0 (value > 0)
        Map<String, Boolean> fulfilled = new HashMap<>();
        for (final PerkNode node : this.api.getNodes()) {
            fulfilled.put(node.getId(), 0.0 < api.nodeValue(p, node.getId()));
        }
        return fulfilled;
    }

    private static Material materialOr(final String name, final Material def) {
        if (null == name || name.isBlank()) return def;
        final Material m = Material.matchMaterial(name, true);
        return null != m ? m : def;
    }

    private static ItemStack named(final Material mat, final Component name, final List<Component> lore) {
        final ItemStack it = new ItemStack(mat);
        final ItemMeta meta = it.getItemMeta();
        meta.displayName(name);
        if (null != lore) meta.lore(lore);
        it.setItemMeta(meta);
        return it;
    }

    /** Holder marker so the listener can distinguish this menu. */
    public static final class Holder implements InventoryHolder {
        @Override public Inventory getInventory() { return null; }
    }

    /** Returns true if the inventory belongs to this GUI. */
    public static boolean isMenu(final Inventory inv) {
        return null != inv && inv.getHolder() instanceof Holder;
    }

    /** NamespacedKey used to tag node ids on item meta. */
    public NamespacedKey nodeKey() {
        return this.KEY_NODE_ID;
    }

    /** Utility for click handler to fetch the node id from a clicked item. */
    public String getNodeIdFrom(final ItemStack clicked) {
        if (null == clicked || !clicked.hasItemMeta()) return null;
        return clicked.getItemMeta().getPersistentDataContainer().get(this.KEY_NODE_ID, PersistentDataType.STRING);
    }
}
