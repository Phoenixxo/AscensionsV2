package dev.phoenixxo.requirements;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface RebirthRequirement {
    boolean isMet(Player player);
    Component getDisplay(Player player);

}