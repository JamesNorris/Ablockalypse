package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.github.JamesNorris.Event.GameCreateEvent;
import com.github.JamesNorris.Event.GameEndEvent;
import com.github.JamesNorris.Event.GameMobDeathEvent;
import com.github.JamesNorris.Event.GameMobSpawnEvent;
import com.github.JamesNorris.Event.GamePlayerJoinEvent;
import com.github.JamesNorris.Event.GamePlayerLeaveEvent;
import com.github.JamesNorris.Event.GameSignClickEvent;
import com.github.JamesNorris.Event.LastStandEvent;
import com.google.common.collect.Maps;

public enum ZAEventType {
	//@formatter:off
	GAMECREATEEVENT(1, "A new Ablockalypse game @game@ has been created by player @player@.", GameCreateEvent.class),
	GAMEENDEVENT(2, "The Ablockalypse game @game@ has ended.", GameEndEvent.class),
	GAMEMOBDEATHEVENT(3, "A mob in the Ablockalypse game @game@ has been killed. There are @mobcount@ mobs left in level @level@.", GameMobDeathEvent.class),
	GAMEMOBSPAWNEVENT(4, "The #@mobcount@ mob has been spawned in level @level@ of the Ablockalypse game @game@.", GameMobSpawnEvent.class),
	GAMEPLAYERJOINEVENT(5, "Player @player@ has joined the Ablockalypse game @game@.", GamePlayerJoinEvent.class),
	GAMEPLAYERLEAVEEVENT(6, "Player @player@ has left the Ablockalypse game @game@.", GamePlayerLeaveEvent.class),
	GAMESIGNCLICKEVENT(7, "The @line2@ sign at @location@ has been clicked by @player@.", GameSignClickEvent.class),
	LASTSTANDEVENT(8, "Player @player@ in game @game@ has been @laststandstatus@.", LastStandEvent.class);
	//@formatter:on
	//
	private final static Map<Integer, ZAEventType> BY_ID = Maps.newHashMap();

	private final Class<?> clazz;

	private final int id;

	private final String xmppmessage;

	ZAEventType(int id, String xmppmessage, Class<?> clazz) {
		this.id = id;
		this.xmppmessage = xmppmessage;
		this.clazz = clazz;
	}

	public Class<?> getAttachedClass() {
		return clazz;
	}

	public int getId() {
		return id;
	}

	public String getXMPPMessage() {
		return xmppmessage;
	}
	
	static {
		for (ZAEventType type : values()) {
			BY_ID.put(type.id, type);
		}
	}
}
