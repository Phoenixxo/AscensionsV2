package phoenixxo.ascensionsV2.requirements;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface AscensionRequirement {
    boolean isMet(Player player);
    Component getDisplay (Player player);
}
