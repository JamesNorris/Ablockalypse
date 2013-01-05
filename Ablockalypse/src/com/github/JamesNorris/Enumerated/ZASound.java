package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.google.common.collect.Maps;

public enum ZASound {
	/**@formatter:off**/
	ACHIEVEMENT(1), DEATH(2), END(3), LAST_STAND(4), NEXT_LEVEL(5), PREV_LEVEL(6), START(7), TELEPORT(8), 
	BARRIER_BREAK(9), BARRIER_REPAIR(10), AREA_BUY(11), AREA_REPLACE(12), EXPLOSION(13);
	/**@formatter:on**/
	private int id;
	private final static Map<Integer, ZASound> BY_ID = Maps.newHashMap();
	
	ZASound(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static ZASound getById(final int id) {
		return BY_ID.get(id);
	}

	static {
		for (ZASound setting : values()) {
			BY_ID.put(setting.id, setting);
		}
	}
}
