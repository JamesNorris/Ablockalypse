package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.google.common.collect.Maps;

public enum ZAColor {
	/**@formatter:off**/
	RED(1, (byte) 14), GREEN(2, (byte) 5), BLUE(3, (byte) 11);
	/**@formatter:on**/
	private int id;
	private byte data;
	private final static Map<Integer, ZAColor> BY_ID = Maps.newHashMap();
	
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
	
	public static ZAColor getById(final int id) {
		return BY_ID.get(id);
	}

	static {
		for (ZAColor setting : values()) {
			BY_ID.put(setting.id, setting);
		}
	}
}
