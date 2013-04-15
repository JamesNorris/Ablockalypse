package com.github.jamesnorris.enumerated;

import java.util.Map;

import com.google.common.collect.Maps;

public enum ZAColor {
    //@formatter:off
	BLUE(3, (byte) 11), 
	GREEN(2, (byte) 5),
	RED(1, (byte) 14),
	ORANGE(4, (byte) 1);
	//@formatter:on
    //
    private final static Map<Integer, ZAColor> BY_ID = Maps.newHashMap();

    public static ZAColor getById(final int id) {
        return BY_ID.get(id);
    }

    private byte data;
    private int id;

    ZAColor(int id, byte data) {
        this.id = id;
        this.data = data;
    }

    public byte getData() {
        return data;
    }

    public int getId() {
        return id;
    }

    static {
        for (ZAColor setting : values()) {
            BY_ID.put(setting.id, setting);
        }
    }
}
