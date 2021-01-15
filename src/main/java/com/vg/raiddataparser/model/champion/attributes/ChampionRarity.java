package com.vg.raiddataparser.model.champion.attributes;

import java.util.HashMap;
import java.util.Map;

public enum ChampionRarity {
    COMMON(1),
    UNCOMMON(2),
    RARE(3),
    EPIC(4),
    LEGENDARY(5);

    public final int code;
    private static final Map<Integer, ChampionRarity> CODE_TO_RARITY = new HashMap<>();

    static {
        for (ChampionRarity rarity : values()) {
            CODE_TO_RARITY.put(rarity.code, rarity);
        }
    }

    ChampionRarity(int code) {
        this.code = code;
    }

    public static String getName(int code) {
        return CODE_TO_RARITY.get(code).toString();
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase().replace("_", " ");
    }

}