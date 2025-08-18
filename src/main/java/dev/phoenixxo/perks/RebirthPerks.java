package dev.phoenixxo.perks;

import dev.phoenixxo.BetterRebirth;
import dev.phoenixxo.perks.api.RebirthPerksAPI;
import dev.phoenixxo.perks.model.PerkGraph;
import dev.phoenixxo.perks.storage.PerkStorage;
import dev.phoenixxo.perks.storage.StoragePdcYaml;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public class RebirthPerks implements Listener {
    private final BetterRebirth plugin = BetterRebirth.getInstance();
    private final PerkGraph graph;
    private final PerkStorage storage;
    @Getter
    private final RebirthPerksAPI api;

    public RebirthPerks() {
        this.graph = new PerkGraph();
        this.storage = new StoragePdcYaml();
        this.api = new RebirthPerksAPI(graph, storage);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void load(FileConfiguration config) {
        graph.loadFromConfig(config);
        storage.load();
    }

    public void save() {
        storage.save();
    }

}
