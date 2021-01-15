package com.vg.raiddataparser.model.champion.attributes;

import java.util.HashMap;
import java.util.Map;

public enum ChampionAffinity {
    MAGIC(1),
    FORCE(2),
    SPIRIT(3),
    VOID(4);

    public final int code;
    private static final Map<Integer, ChampionAffinity> CODE_TO_AFFINITY = new HashMap<>();

    static {
        for (ChampionAffinity affinity : values()) {
            CODE_TO_AFFINITY.put(affinity.code, affinity);
        }
    }

    ChampionAffinity(int code) {
        this.code = code;
    }

    public static String getName(int code) {
        return CODE_TO_AFFINITY.get(code).toString();
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase().replace("_", " ");
    }

}