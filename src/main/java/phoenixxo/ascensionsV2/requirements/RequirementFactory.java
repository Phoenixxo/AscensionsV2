package phoenixxo.ascensionsV2.requirements;

import dev.drawethree.xprison.api.XPrisonAPI;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class RequirementFactory {

    private final XPrisonAPI api;
    private final Map<String, BiFunction<String, XPrisonAPI, AscensionRequirement>> registered;

    public RequirementFactory(XPrisonAPI api) {
        this.api = api;
        this.registered = new HashMap<>();

        register("rank", RankRequirement::new);
        register("prestige", PrestigeRequirement::new);
        register("money", MoneyRequirement::new);
    }

    public void register(String key, BiFunction<String, XPrisonAPI, AscensionRequirement> constructor) {
        registered.put(key.toLowerCase(), constructor);
    }

    public AscensionRequirement create(String type, String value) {
        var func = registered.get(type.toLowerCase());
        if (func == null) {
            Bukkit.getLogger().warning("Unknown requirement type: " + type);
            return null;
        }
        return func.apply(value, api);
    }

}
