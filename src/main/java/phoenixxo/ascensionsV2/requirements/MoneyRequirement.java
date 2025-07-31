package phoenixxo.ascensionsV2.requirements;

import dev.drawethree.xprison.api.XPrisonAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class MoneyRequirement implements AscensionRequirement{

    private final double requiredAmount;
    private final XPrisonAPI api;

    public MoneyRequirement(String requiredAmount, XPrisonAPI api) {
        this.requiredAmount = Double.parseDouble(requiredAmount);
        this.api = api;
    }

    @Override
    public boolean isMet(Player player) {
        return api.getCurrencyApi().getBalance(player, "money") >= requiredAmount;
    }

    @Override
    public Component getDisplay(Player player) {
        double current = api.getCurrencyApi().getBalance(player, "money");
        return Component.text("Balance: $", NamedTextColor.WHITE)
                .append(Component.text(String.format("%.2f", current), NamedTextColor.WHITE))
                .append(Component.text(" / Required: $", NamedTextColor.WHITE))
                .append(Component.text(String.format("%.2f", requiredAmount), NamedTextColor.WHITE));
    }
}
