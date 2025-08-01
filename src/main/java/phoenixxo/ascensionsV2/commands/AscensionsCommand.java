package phoenixxo.ascensionsV2.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import phoenixxo.ascensionsV2.AscensionsV2;
import phoenixxo.ascensionsV2.requirements.RequirementFactory;
import phoenixxo.ascensionsV2.util.MessageUtil;

public class AscensionsCommand implements CommandExecutor {

    private final AscensionsV2 plugin;

    public AscensionsCommand(AscensionsV2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("gui")) {
            // GUI
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(MessageUtil.get("general.player-only"));
                return true;
            }
            if (!player.hasPermission("ascensionsv2.ascend")) {
                player.sendMessage(MessageUtil.get("general.no-permission"));
                return true;
            }
            plugin.getAscensionGUI().openGUI(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!commandSender.hasPermission("ascensionsv2.reload")) {
                commandSender.sendMessage(MessageUtil.get("general.no-permission"));
                return true;
            }

            plugin.getAscensionLevelManager().save();
            plugin.getMessagesManager().saveMessagesConfig();

            plugin.reloadConfig();
            plugin.setRequirementFactory(new RequirementFactory(plugin.getPrisonAPI()));
            plugin.getAscensionManager().loadRequirements();
            plugin.getAscensionLevelManager().reload();
            plugin.getMessagesManager().reload();
            plugin.getPrefixManager().reload(plugin);

            commandSender.sendMessage(MessageUtil.get("general.reload-complete"));
            return true;
        }

        if (args[0].equalsIgnoreCase("level")) {
            if (args.length == 2 && args[1].equalsIgnoreCase("get")) {
                if (!(commandSender instanceof Player player)) {
                    commandSender.sendMessage(MessageUtil.get("general.player-only"));
                    return true;
                }

                int level = plugin.getAscensionLevelManager().getAscensionLevel(player.getUniqueId());
                player.sendMessage(MessageUtil.get("general.level-get", "%level%", Integer.toString(level)));
                return true;
            } else if (args.length == 4 && args[1].equalsIgnoreCase("set")) {
                if (!commandSender.hasPermission("ascensionsv2.setlevel")) {
                    commandSender.sendMessage(MessageUtil.get("general.no-permission"));
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[2]);
                if (target == null) {
                    commandSender.sendMessage(MessageUtil.get("general.player-not-found"));
                    return true;
                }

                try {
                    int level = Integer.parseInt(args[3]);
                    plugin.getAscensionLevelManager().setAscensionLevel(target.getUniqueId(), level);
                    commandSender.sendMessage(MessageUtil.get("general.level-set", "%player%", target.getName(), "%level%", Integer.toString(level)));
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(MessageUtil.get("general.invalid-number"));
                }
                return true;
            }
        }

        commandSender.sendMessage(MessageUtil.get("general.usage"));
        return true;
    }
}
