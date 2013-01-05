package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.google.common.collect.Maps;

public enum ZAEffect {
	/**@formatter:off**/
	SMOKE(1), FLAMES(2), POTION_BREAK(3), EXTINGUISH(4), WOOD_BREAK(5), IRON_BREAK(6), TELEPORTATION(7), LIGHTNING(8), BEACON(9);
	/**@formatter:on**/
	private int id;
	private final static Map<Integer, ZAEffect> BY_ID = Maps.newHashMap();
	
	ZAEffect(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static ZAEffect getById(final int id) {
		return BY_ID.get(id);
	}

	static {
		for (ZAEffect setting : values()) {
			BY_ID.put(setting.id, setting);
		}
	}
}
