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
    DEADSHOT_DAIQUIRI_COST("deadshotDaiquiriPoints", Integer.class), 
    DEADSHOT_DAIQUIRI_LEVEL("deadshotDaiquiriLevel", Integer.class), 
    DEBUG("DEBUG", Boolean.class), 
    DEFAULT_FRIENDLY_FIRE_MODE("defaultFriendlyFireMode", Boolean.class), 
    ENABLE_AUTO_UPDATE("ENABLE_AUTO_UPDATE", Boolean.class), 
    END_ON_LAST_PLAYER_LAST_STAND("endOnLastPlayerLastStand", Boolean.class),
    EXPLOSIVE_KILL_PAY("explosivePointIncrease", Integer.class), 
    EXTRA_EFFECTS("addedEffects", Boolean.class), 
    EXTRA_SOUNDS("addedSounds", Boolean.class),
    JUGGERNOG_COST("juggernogPoints", Integer.class),
    JUGGERNOG_LEVEL("juggernogLevel", Integer.class), 
    KILL_PAY("pointIncrease", Integer.class), 
    LAST_STAND_HEALTH_THRESHOLD("lastStandThreshold", Integer.class), 
    LAST_STAND_HELPER_PAY("pointsGivenOnHelp", Integer.class),
    LEVEL_TRANSITION_TIME("levelTransitionTime", Integer.class),
    LOSE_PERKS_ON_LAST_STAND("losePerksOnLastStand", Boolean.class), 
    MAX_PLAYERS("maxPlayers", Integer.class), 
    MAX_WAVE("maxWave", Integer.class), 
    MOVING_CHESTS("movingChests", Boolean.class), 
    PACK_A_PUNCH_COST("packapunchCost", Integer.class), 
    PACK_A_PUNCH_LEVEL("packapunchLevel", Integer.class), 
    PERK_DURATION("perkDuration", Integer.class), 
    PHD_FLOPPER_COST("phdFlopperPoints", Integer.class), 
    PHD_FLOPPER_LEVEL("phdFlopperLevel", Integer.class), 
    POWERUP_CHANCE("powerupChance", Integer.class),
    STAMINUP_COST("staminupPoints", Integer.class), 
    STAMINUP_LEVEL("staminupLevel", Integer.class),
    STARTING_POINTS("startPoints", Integer.class),
    TELEPORT_TIME("teleportTime", Integer.class), 
    UPDATE_VERSION("UPDATE_VERSION", String.class), 
    WOLF_LEVELS("wolfLevels", List.class),
    XMPP_GAME_CREATE("xmppAnnounceGameCreate", Boolean.class), 
    XMPP_GAME_END("xmppAnnounceGameEnd", Boolean.class), 
    XMPP_LAST_STAND("xmppAnnounceLastStand", Boolean.class),
    XMPP_MOB_DEATH("xmppAnnounceGameMobDeath", Boolean.class),
    XMPP_MOB_SPAWN("xmppAnnounceGameMobSpawn", Boolean.class),
    XMPP_PLAYER_JOIN("xmppAnnouncePlayerJoinGame", Boolean.class),
    XMPP_PLAYER_LEAVE("xmppAnnouncePlayerLeaveGame", Boolean.class),
    XMPP_SIGN_CLICK("xmppAnnounceGameSignClick", Boolean.class);
	//@formatter:on
    //
    private final static Map<Integer, Setting> BY_ID = Maps.newHashMap();

    static {
        int id = 0;
        for (Setting setting : values()) {
            BY_ID.put(++id, setting);
        }
    }

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
        setting = name;
        this.type = type;
        object = Ablockalypse.instance.getConfig().get(name);
        if (object == null) {
            Ablockalypse.crash("The value for <" + toString() + ">, which uses the value of <" + name + "> in the configuration is null or missing!", false);
        }
    }

    public String getName() {
        return setting;
    }

    public Object getSetting() {
        return object;
    }

    public Class<?> getType() {
        return type;
    }

    public void set(Object object) {
        this.object = object;
    }
}
