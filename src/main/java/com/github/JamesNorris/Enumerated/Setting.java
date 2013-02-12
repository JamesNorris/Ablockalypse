package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.github.Ablockalypse;
import com.google.common.collect.Maps;

public enum Setting {
    //@formatter:off
	ATOMPOINTS(1, "pointsGivenOnAtomBomb"), BARRIERFULLFIX(2, "barrierCompleteFixIncrease"), BARRIERPARTFIX(3, "barrierPerFixIncrease"), BEACONS(4, "chestBeacons"), 
	BLINKERS(5, "blinkers"), CHESTCOST(41, "mysteryChestCost"), CLEARMOBS(11, "clearNearbyMobs"), DAMAGELEVEL(12, "damageLevel"), 
	DAMAGEPOINTS(13, "damagePoints"), DEBUG(14, "DEBUG"), DEFAULTFRIENDLYFIREMODE(23, "defaultFriendlyFireMode"), DIAMONDSWORDCOST(15, "diamondSword"), 
	DIAMONDSWORDLEVEL(16, "diamondSwordLevel"), DOUBLESPEEDLEVEL(17, "doubleSpeedLevel"), ENABLEAUTOUPDATE(19, "ENABLE_AUTO_UPDATE"), ENCHDAMAGECOST(20, "enchDamageCost"), ENCHRANDOMCOST(21, "enchRandomCost"), 
	EXTRAEFFECTS(22, "addedEffects"), GOLDSWORDCOST(24, "goldSword"), GOLDSWORDLEVEL(25, "goldSwordLevel"), GRENADECOST(26, "perGrenade"), GRENADELEVEL(27, "grenadeLevel"), 
	HEALLEVEL(28, "healLevel"), HEALPOINTS(29, "healPoints"), IRONSWORDCOST(36, "ironSword"), IRONSWORDLEVEL(37, "ironSwordLevel"), 
	JUGGERNAUTLEVEL(58, "juggernautLevel"), JUGGERNAUTPOINTS(57, "juggernautPoints"), KILLPOINTINCREASE(44, "pointIncrease"), LASTSTANDHELPPOINTS(34, "pointsGivenOnHelp"), LASTSTANDTHRESHOLD(39, "lastStandThreshold"), 
	LOSEPERKSONLASTSTAND(38, "losePerksOnLastStand"), MAXPLAYERS(40, "maxPlayers"), MOVINGCHESTS(42, "movingChests"), 
	PACKAPUNCHLEVEL(43, "packapunchLevel"), PERKDURATION(18, "perkDuration"), POWERUPCHANCE(45, "powerupChance"), REGENERATIONLEVEL(46, "regenLevel"), 
	REGENERATIONPOINTS(47, "regenPoints"), SPEEDLEVEL(48, "speedLevel"), SPEEDPOINTS(49, "speedPoints"), STARTINGBOOTS(33, "startingItems.armor.boots"), 
	STARTINGCHESTPLATE(31, "startingItems.armor.chestplate"), STARTINGHELMET(30, "startingItems.armor.helmet"), STARTINGINVENTORY(35, "pointsGivenOnHelp"), STARTINGLEGGINGS(32, "startingItems.armor.leggings"), 
	STARTINGPOINTS(50, "startPoints"), STONESWORDCOST(51, "stoneSword"), STONESWORDLEVEL(52, "stoneSwordLevel"), TELEPORTTIME(53, "teleportTime"), WOLFLEVELS(54, "wolfLevels"), 
	WOODSWORDCOST(55, "woodSword"), WOODSWORDLEVEL(56, "woodSwordLevel"), XMPPGAMEEND(7, "xmppAnnounceGameEnd"), XMPPGAMESTART(6, "xmppAnnounceGameStart"), XMPPLASTSTAND(10, "xmppAnnounceLastStand"), 
	XMPPPLAYERJOIN(8, "xmppAnnouncePlayerJoinGame"), XMPPPLAYERLEAVE(9, "xmppAnnouncePlayerLeaveGame");
	//@formatter:on
    //
    private final static Map<Integer, Setting> BY_ID = Maps.newHashMap();

    public static Setting getById(final int id) {
        return BY_ID.get(id);
    }

    public static int getHighestId() {
        return BY_ID.size();
    }

    private int id;
    private Object object;
    private String setting;

    Setting(int id, String name) {
        this.id = id;
        this.setting = name;
        object = Ablockalypse.instance.getConfig().get(name);
    }

    public int getId() {
        return id;
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
        for (Setting setting : values())
            BY_ID.put(setting.id, setting);
    }
}
