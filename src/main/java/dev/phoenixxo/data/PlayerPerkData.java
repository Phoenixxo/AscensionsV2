package dev.phoenixxo.data;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PlayerPerkData {
    @Getter
    private final Map<String, Integer> ranks;
    @Getter
    @Setter
    private int spentPoints;

    public PlayerPerkData(int spentPoints, Map<String, Integer> ranks) {
        this.spentPoints = spentPoints;
        this.ranks = (ranks == null) ? new HashMap<>() : new HashMap<>(ranks);
    }

    public Map<String, Integer> getRanksView() {
        return Collections.unmodifiableMap(this.ranks);
    }
}
