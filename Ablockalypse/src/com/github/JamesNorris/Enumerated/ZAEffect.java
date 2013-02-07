package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.google.common.collect.Maps;

public enum ZAEffect {
    //@formatter:off
	BEACON(9), EXTINGUISH(4), FLAMES(2), IRON_BREAK(6), LIGHTNING(8), POTION_BREAK(3),
	SMOKE(1), TELEPORTATION(7), WOOD_BREAK(5);
	//@formatter:on
    //
    private final static Map<Integer, ZAEffect> BY_ID = Maps.newHashMap();

    public static ZAEffect getById(final int id) {
        return BY_ID.get(id);
    }

    private int id;

    ZAEffect(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    static {
        for (ZAEffect setting : values()) {
            BY_ID.put(setting.id, setting);
        }
    }
}
