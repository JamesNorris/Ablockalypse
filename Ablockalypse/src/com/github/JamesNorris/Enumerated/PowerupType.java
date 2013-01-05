package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.google.common.collect.Maps;

public enum PowerupType {
	/**@formatter:off**/
	ATOM_BOMB(1), BARRIER_FIX(2), WEAPON_FIX(3), INSTA_KILL(4);
	/**@formatter:on**/
	private int id;
	private final static Map<Integer, PowerupType> BY_ID = Maps.newHashMap();
	
	PowerupType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static PowerupType getById(final int id) {
		return BY_ID.get(id);
	}

	static {
		for (PowerupType setting : values()) {
			BY_ID.put(setting.id, setting);
		}
	}
}
