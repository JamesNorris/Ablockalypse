package com.github.jamesnorris.enumerated;

import java.awt.List;
import java.util.Map;

import com.github.Ablockalypse;
import com.google.common.collect.Maps;

public enum Setting {
    //@formatter:off
    ATOM_BOMB_POINTS("pointsGivenOnAtomBomb", Integer.class), 
    BARRIER_FULL_FIX_PAY("barrierCompleteFixIncrease", Integer.class), 
    BARRIER_PART_FIX_PAY("barrierPerFixIncrease", Integer.class), 
    BEACONS("chestBeacons", Boolean.class), 
    BLINKERS("blinkers", Boolean.class), 
    CHEST_COST("mysteryChestCost", Integer.class), 
    CLEAR_MOBS("clearNearbyMobs", Boolean.class), 
    CLEAR_MOBS_RADIUS("nearbyMobClearingRadius", Integer.class),
    DEADSHOT_DAIQUIRI_LEVEL("deadshotDaiquiriLevel", Integer.class), 
    DEADSHOT_DAIQUIRI_COST("deadshotDaiquiriPoints", Integer.class), 
    DEBUG("DEBUG", Boolean.class), 
    DEFAULT_FRIENDLY_FIRE_MODE("defaultFriendlyFireMode", Boolean.class), 
    DOUBLE_SPEED_LEVEL("doubleSpeedLevel", Integer.class), 
    ENABLE_AUTO_UPDATE("ENABLE_AUTO_UPDATE", Boolean.class), 
    PACK_A_PUNCH_COST("packapunchCost", Integer.class), 
    EXTRA_EFFECTS("addedEffects", Boolean.class), 
    EXTRA_SOUNDS("addedSounds", Boolean.class),
    MAX_WAVE("maxWave", Integer.class),
    JUGGERNOG_LEVEL("juggernogLevel", Integer.class), 
    JUGGERNOG_COST("juggernogPoints", Integer.class), 
    KILL_PAY("pointIncrease", Integer.class), 
    EXPLOSIVE_KILL_PAY("explosivePointIncrease", Integer.class),
    LAST_STAND_HELPER_PAY("pointsGivenOnHelp", Integer.class), 
    LAST_STAND_HEALTH_THRESHOLD("lastStandThreshold", Integer.class), 
    LOSE_PERKS_ON_LAST_STAND("losePerksOnLastStand", Boolean.class), 
    MAX_PLAYERS("maxPlayers", Integer.class), 
    MOVING_CHESTS("movingChests", Boolean.class), 
    PACK_A_PUNCH_LEVEL("packapunchLevel", Integer.class), 
    PERK_DURATION("perkDuration", Integer.class), 
    POWERUP_CHANCE("powerupChance", Integer.class), 
    STAMINUP_LEVEL("staminupLevel", Integer.class), 
    STAMINUP_COST("staminupPoints", Integer.class),
    STARTING_POINTS("startPoints", Integer.class), 
    PHD_FLOPPER_LEVEL("phdFlopperLevel", Integer.class),
    PHD_FLOPPER_COST("phdFlopperPoints", Integer.class),
    TELEPORT_TIME("teleportTime", Integer.class), 
    WOLF_LEVELS("wolfLevels", List.class), 
    END_ON_LAST_PLAYER_LAST_STAND("endOnLastPlayerLastStand", Boolean.class),
    XMPP_GAME_CREATE("xmppAnnounceGameCreate", Boolean.class), 
    XMPP_GAME_END("xmppAnnounceGameEnd", Boolean.class), 
    XMPP_MOB_SPAWN("xmppAnnounceGameMobSpawn", Boolean.class),
    XMPP_MOB_DEATH("xmppAnnounceGameMobDeath", Boolean.class),
    XMPP_SIGN_CLICK("xmppAnnounceGameSignClick", Boolean.class),
    XMPP_PLAYER_JOIN("xmppAnnouncePlayerJoinGame", Boolean.class),
    XMPP_PLAYER_LEAVE("xmppAnnouncePlayerLeaveGame", Boolean.class),
    XMPP_LAST_STAND("xmppAnnounceLastStand", Boolean.class);
	//@formatter:on
    //
    private final static Map<Integer, Setting> BY_ID = Maps.newHashMap();

    public static Setting getById(final int id) {
        return BY_ID.get(id);
    }

    public static int getHighestId() {
        return BY_ID.size();
    }

    private Object object;
    private String setting;
    private Class<?> type;

    Setting(String name, Class<?> type) {
        this.setting = name;
        this.type = type;
        object = Ablockalypse.instance.getConfig().get(name);
    }
    
    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return setting;
    }

    public Object getSetting() {
        return object;
    }

    public void set(Object object) {
        this.object = object;
    }

    static {
        int id = 0;
        for (Setting setting : values())
            BY_ID.put(++id, setting);
    }
}
