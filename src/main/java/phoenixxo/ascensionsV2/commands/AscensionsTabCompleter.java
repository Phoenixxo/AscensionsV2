package phoenixxo.ascensionsV2.commands;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class AscensionsTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("gui", "reload", "level");

    private static final List<String> LEVEL_SUBS = Arrays.asList("get", "set");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], SUBCOMMANDS, new ArrayList<>());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("level")) {
            return StringUtil.copyPartialMatches(args[1], LEVEL_SUBS, new ArrayList<>());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("level") && args[1].equalsIgnoreCase("set")) {
            List<String> names = new ArrayList<>();
            for (Player p: Bukkit.getOnlinePlayers()) {
                names.add(p.getName());
            }
            return StringUtil.copyPartialMatches(args[2], names, new ArrayList<>());
        }

        return Collections.emptyList();
    }
}
