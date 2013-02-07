package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.google.common.collect.Maps;

public enum GameEntityType {
    //@formatter:off
	HELLHOUND(2),
	UNDEAD(1);
	//@formatter:on
    //
    private final static Map<Integer, GameEntityType> BY_ID = Maps.newHashMap();

    public static GameEntityType getById(final int id) {
        return BY_ID.get(id);
    }

    private int id;

    GameEntityType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    static {
        for (GameEntityType setting : values()) {
            BY_ID.put(setting.id, setting);
        }
    }
}
