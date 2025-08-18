package dev.phoenixxo.perks.model;

import lombok.Getter;

import java.util.List;

@Getter
public class PerkNode {
    private final String id;
    private final String name;
    private final String icon; // Material name
    private final String category; // Mining / Economy etc.
    private final int slot; // Gui Slot
    private final int maxRank;
    private final int costPerRank;

    private final String bonusType; // How it increments (%, *, +)
    private final double base;
    private final double perRank;
    private final double max;

    private final List<String> requires;
    private final List<String> neighbors;

    public PerkNode(String id,
                    String name,
                    String icon,
                    String category,
                    int slot,
                    int maxRank,
                    int costPerRank,
                    String bonusType,
                    double base,
                    double perRank,
                    double max,
                    List<String> requires,
                    List<String> neighbors) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.category = category;
        this.slot = slot;
        this.maxRank = maxRank;
        this.costPerRank = costPerRank;
        this.bonusType = bonusType;
        this.base = base;
        this.perRank = perRank;
        this.max = max;
        this.requires = requires;
        this.neighbors = neighbors;
    }

    public double valueForRank(int rank) {
        if (rank <= 0) return 0.0;
        double v = base + perRank * (rank - 1);
        return Math.min(v, max);
    }
}
