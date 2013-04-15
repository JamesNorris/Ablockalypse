package com.github.jamesnorris.enumerated;

import java.util.Map;

import com.github.jamesnorris.event.GameCreateEvent;
import com.github.jamesnorris.event.GameEndEvent;
import com.github.jamesnorris.event.GameMobDeathEvent;
import com.github.jamesnorris.event.GameMobSpawnEvent;
import com.github.jamesnorris.event.GamePlayerJoinEvent;
import com.github.jamesnorris.event.GamePlayerLeaveEvent;
import com.github.jamesnorris.event.GameSignClickEvent;
import com.github.jamesnorris.event.LastStandEvent;
import com.google.common.collect.Maps;

public enum ZAEventType {
    //@formatter:off
	GAME_CREATE_EVENT(1, "A new Ablockalypse game @game@ has been created by player @player@.", GameCreateEvent.class),
	GAME_END_EVENT(2, "The Ablockalypse game @game@ has ended.", GameEndEvent.class),
	GAME_MOB_DEATH_EVENT(3, "A mob in the Ablockalypse game @game@ has been killed. There are @mobcount@ mobs left in level @level@.", GameMobDeathEvent.class),
	GAME_MOB_SPAWN_EVENT(4, "The #@mobcount@ mob has been spawned in level @level@ of the Ablockalypse game @game@.", GameMobSpawnEvent.class),
	GAME_PLAYER_JOIN_EVENT(5, "Player @player@ has joined the Ablockalypse game @game@.", GamePlayerJoinEvent.class),
	GAME_PLAYER_LEAVE_EVENT(6, "Player @player@ has left the Ablockalypse game @game@.", GamePlayerLeaveEvent.class),
	GAME_SIGN_CLICK_EVENT(7, "The @line2@ sign at @location@ has been clicked by @player@.", GameSignClickEvent.class),
	GAME_PLAYER_LAST_STAND_EVENT(8, "Player @player@ in game @game@ has been @laststandstatus@.", LastStandEvent.class);
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
