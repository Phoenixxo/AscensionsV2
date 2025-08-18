package dev.phoenixxo.commands;

import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.requirements.RequirementFactory;
import dev.phoenixxo.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RebirthCommand implements CommandExecutor {

    private final BetterRebirth plugin;

    public RebirthCommand(final BetterRebirth plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String s, @NotNull final String[] args) {
        if (0 == args.length) {
            if (!(commandSender instanceof final Player player)) {
                commandSender.sendMessage(MessageUtil.get("general.player-only"));
                return true;
            }

            this.plugin.getRebirthGUI().openGUI(player);
            return true;

        } else if ("gui".equalsIgnoreCase(args[0])) {
            // GUI
            if (!(commandSender instanceof final Player player)) {
                commandSender.sendMessage(MessageUtil.get("general.player-only"));
                return true;
            }

            if (1 == args.length) {
                plugin.getRebirthGUI().openGUI(player);
                return true;
            }

            if (args[1].equalsIgnoreCase("rebirth")) {
                plugin.getRebirthGUI().openGUI(player);
                return true;
            } else if (args[1].equalsIgnoreCase("perks")) {
                plugin.getPerkTreeGUI().open(player);
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!commandSender.hasPermission("better-rebirth.reload")) {
                commandSender.sendMessage(MessageUtil.get("general.no-permission"));
                return true;
            }

            final long start = System.currentTimeMillis();
            try {
                this.plugin.getDataService().reload();
                this.plugin.getPlayerDataManager().reload();
                this.plugin.reloadConfig();
                this.plugin.getMessagesManager().reload();
                this.plugin.setRequirementFactory(new RequirementFactory(this.plugin.getPrisonAPI()));
                this.plugin.getRebirthManager().loadRequirements();
                this.plugin.getPrefixManager().reload(this.plugin);
                this.plugin.getRebirthPerks().save();

                commandSender.sendMessage(MessageUtil.get("general.reload-complete"));
            } catch (final Exception e) {
                this.plugin.getLogger().severe("Reload failed: " + e.getMessage());
                e.printStackTrace();
                commandSender.sendMessage(MessageUtil.get("general.reload-failed"));
            } finally {
                final long elapsed = System.currentTimeMillis() - start;
                this.plugin.getLogger().info("Reload completed in " + elapsed + " ms.");
            }

            return true;

        }

        if ("level".equalsIgnoreCase(args[0])) {
            if (2 == args.length && "get".equalsIgnoreCase(args[1])) {
                if (!(commandSender instanceof final Player player)) {
                    commandSender.sendMessage(MessageUtil.get("general.player-only"));
                    return true;
                }

                final int level = this.plugin.getRebirthLevelManager().getRebirthLevel(player.getUniqueId());
                player.sendMessage(MessageUtil.get("general.level-get", "%level%", Integer.toString(level)));
                return true;
            } else if (4 == args.length && "set".equalsIgnoreCase(args[1])) {
                if (!commandSender.hasPermission("better-rebirth.setlevel")) {
                    commandSender.sendMessage(MessageUtil.get("general.no-permission"));
                    return true;
                }

                final Player target = Bukkit.getPlayerExact(args[2]);
                if (null == target) {
                    commandSender.sendMessage(MessageUtil.get("general.player-not-found"));
                    return true;
                }

                try {
                    final int level = Integer.parseInt(args[3]);
                    this.plugin.getRebirthLevelManager().setRebirthLevel(target.getUniqueId(), level);
                    commandSender.sendMessage(MessageUtil.get("general.level-set", "%player%", target.getName(), "%level%", Integer.toString(level)));
                } catch (final NumberFormatException e) {
                    commandSender.sendMessage(MessageUtil.get("general.invalid-number"));
                }
                return true;
            }
        }

        commandSender.sendMessage(MessageUtil.get("general.usage"));
        return true;
    }
}
