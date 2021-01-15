package com.vg.raiddataparser.model.champion.attributes;

import java.util.HashMap;
import java.util.Map;

public enum ChampionFaction {
    BANNER_LORDS(1),
    HIGH_ELVES(2),
    SACRED_ORDER(3),
    OGRYN_TRIBES(5),
    LIZARDMEN(6),
    SKINWALKERS(7),
    ORCS(8),
    DEMONSPAWN(9),
    UNDEAD_HORDES(10),
    DARK_ELVES(11),
    KNIGHT_REVENANT(12),
    BARBARIANS(13),
    DWARVES(16);

    public final int code;
    private static final Map<Integer, ChampionFaction> CODE_TO_FACTION = new HashMap<>();

    static {
        for (ChampionFaction faction : values()) {
            CODE_TO_FACTION.put(faction.code, faction);
        }
    }

    ChampionFaction(int code) {
        this.code = code;
    }

    public static String getName(int code) {
        return CODE_TO_FACTION.get(code).toString();
    }

    @Override
    public String toString() {
        String[] words = name().split("_");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].charAt(0) + words[i].substring(1).toLowerCase();
        }
        return String.join(" ", words);
    }

}