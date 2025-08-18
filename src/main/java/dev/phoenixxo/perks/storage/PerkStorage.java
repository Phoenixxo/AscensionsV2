package dev.phoenixxo.perks.storage;

import dev.phoenixxo.perks.model.PlayerPerkState;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface PerkStorage {
    void load();
    void save();

    PlayerPerkState state(UUID uuid);
    void put(PlayerPerkState state);

    default PlayerPerkState state (Player p) {
        return state(p.getUniqueId());
    }
}
