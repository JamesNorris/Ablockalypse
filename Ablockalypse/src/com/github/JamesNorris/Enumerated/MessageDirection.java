package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.google.common.collect.Maps;

public enum MessageDirection {
	//@formatter:off
	XMPP(1),
	PLAYER_PRIVATE(2),
	PLAYER_BROADCAST(3),
	CONSOLE_OUTPUT(4),
	CONSOLE_ERROR(5);
	//@formatter:on
	//	
	private final static Map<Integer, MessageDirection> BY_ID = Maps.newHashMap();
	private final int id;
	
	MessageDirection(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public MessageDirection getById(int id) {
		return BY_ID.get(id);
	}
	
	static {
		for (MessageDirection direction : values()) {
			BY_ID.put(direction.id, direction);
		}
	}
}
