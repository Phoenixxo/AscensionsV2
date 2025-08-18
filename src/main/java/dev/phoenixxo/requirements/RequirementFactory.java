package dev.phoenixxo.requirements;

import dev.drawethree.xprison.api.XPrisonAPI;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class RequirementFactory {

    private final XPrisonAPI api;
    private final Map<String, BiFunction<String, XPrisonAPI, RebirthRequirement>> registered;

    public RequirementFactory(XPrisonAPI api) {
        this.api = api;
        this.registered = new HashMap<>();
        register("rank", RankRequirement::new);
        register("prestige", PrestigeRequirement::new);
        register("cost", CostRequirement::new);
    }

    public void register(String key, BiFunction<String, XPrisonAPI, RebirthRequirement> constructor) {
        registered.put(key.toLowerCase(), constructor);
    }

    public RebirthRequirement create(String type, String value) {
        var func = registered.get(type.toLowerCase());
        if (func == null) {
            Bukkit.getLogger().warning("Unknown requirement type: " + type);
            return null;
        }
        return func.apply(value, api);
    }

}
