package dev.phoenixxo.perks.model;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerPerkState {
    private final UUID uuid;
    public final Map<String, Integer> ranks = new HashMap<>();
    private int spentPoints = 0;

    public PlayerPerkState(UUID uuid) {this.uuid = uuid ;}

    public int rankOf(String nodeId) { return ranks.getOrDefault(nodeId, 0) ; }
    public void rankUp(String nodeId) { ranks.put(nodeId, rankOf(nodeId) + 1) ; }

    public Map<String,Integer> getRanks() { return Collections.unmodifiableMap(ranks); }

    public void addSpent(int cost) { spentPoints += cost ;}
}
