package com.vg.raiddataparser.model.champion.attributes;

import java.util.HashMap;
import java.util.Map;

public enum ChampionRole {
    ATTACK(0),
    DEFENSE(1),
    HP(2),
    SUPPORT(3);

    public final int code;
    private static final Map<Integer, ChampionRole> CODE_TO_ROLE = new HashMap<>();

    static {
        for (ChampionRole role : values()) {
            CODE_TO_ROLE.put(role.code, role);
        }
    }

    ChampionRole(int code) {
        this.code = code;
    }

    public static String getName(int code) {
        return code == 2 ? "HP" : CODE_TO_ROLE.get(code).toString();
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase().replace("_", " ");
    }
}
