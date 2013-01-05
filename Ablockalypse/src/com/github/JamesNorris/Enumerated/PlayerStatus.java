package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.google.common.collect.Maps;

public enum PlayerStatus {
	/**@formatter:off**/
	LAST_STAND(1), LIMBO(2), TELEPORTING(3);
	/**@formatter:on**/
	private int id;
	private final static Map<Integer, PlayerStatus> BY_ID = Maps.newHashMap();
	
	PlayerStatus(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static PlayerStatus getById(final int id) {
		return BY_ID.get(id);
	}

	static {
		for (PlayerStatus setting : values()) {
			BY_ID.put(setting.id, setting);
		}
	}
}
