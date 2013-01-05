package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.google.common.collect.Maps;

public enum GameEntityType {
	/**@formatter:off**/
	UNDEAD(1), HELLHOUND(2);
	/**@formatter:on**/
	private int id;
	private final static Map<Integer, GameEntityType> BY_ID = Maps.newHashMap();
	
	GameEntityType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static GameEntityType getById(final int id) {
		return BY_ID.get(id);
	}

	static {
		for (GameEntityType setting : values()) {
			BY_ID.put(setting.id, setting);
		}
	}
}
