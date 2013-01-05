package com.github.JamesNorris.Enumerated;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.Ablockalypse;
import com.google.common.collect.Maps;

public enum Setting {
	/**@formatter:off**/
	ATOMPOINTS(1, "pointsGivenOnAtomBomb"), BARRIERFULLFIX(2, "barrierCompleteFixIncrease"), BARRIERPARTFIX(3, "barrierPerFixIncrease"), BEACONS(4, "chestBeacons"), 
	BLINKERS(5, "blinkers"), XMPPGAMESTART(6, "xmppAnnounceGameStart"), XMPPGAMEEND(7, "xmppAnnounceGameEnd"), XMPPPLAYERJOIN(8, "xmppAnnouncePlayerJoinGame"), 
	XMPPPLAYERLEAVE(9, "xmppAnnouncePlayerLeaveGame"), XMPPLASTSTAND(10, "xmppAnnounceLastStand"), CLEARMOBS(11, "clearNearbyMobs"), DAMAGELEVEL(12, "damageLevel"), 
	DAMAGEPOINTS(13, "damagePoints"), DEBUG(14, "DEBUG"), DIAMONDSWORDCOST(15, "diamondSword"), DIAMONDSWORDLEVEL(16, "diamondSwordLevel"), DOUBLESPEEDLEVEL(17, "doubleSpeedLevel"), 
	PERKDURATION(18, "perkDuration"), ENABLEAUTOUPDATE(19, "ENABLE_AUTO_UPDATE"), ENCHDAMAGECOST(20, "enchDamageCost"), ENCHRANDOMCOST(21, "enchRandomCost"), EXTRAEFFECTS(22, "addedEffects"), 
	DEFAULTFRIENDLYFIREMODE(23, "defaultFriendlyFireMode"), GOLDSWORDCOST(24, "goldSword"), GOLDSWORDLEVEL(25, "goldSwordLevel"), GRENADECOST(26, "perGrenade"), 
	GRENADELEVEL(27, "grenadeLevel"), HEALLEVEL(28, "healLevel"), HEALPOINTS(29, "healPoints"), STARTINGHELMET(30, "startingItems.armor.helmet"), STARTINGCHESTPLATE(31, "startingItems.armor.chestplate"), 
	STARTINGLEGGINGS(32, "startingItems.armor.leggings"), STARTINGBOOTS(33, "startingItems.armor.boots"), LASTSTANDHELPPOINTS(34, "pointsGivenOnHelp"), 
	STARTINGINVENTORY(35, "pointsGivenOnHelp"), IRONSWORDCOST(36, "ironSword"), IRONSWORDLEVEL(37, "ironSwordLevel"), LOSEPERKSONLASTSTAND(38, "losePerksOnLastStand"), 
	LASTSTANDTHRESHOLD(39, "lastStandThreshold"), MAXPLAYERS(40, "maxPlayers"), CHESTCOST(41, "mysteryChestCost"), MOVINGCHESTS(42, "movingChests"), 
	PACKAPUNCHLEVEL(43, "packapunchLevel"), KILLPOINTINCREASE(44, "pointIncrease"), POWERUPCHANCE(45, "powerupChance"), REGENERATIONLEVEL(46, "regenLevel"), 
	REGENERATIONPOINTS(47, "regenPoints"), SPEEDLEVEL(48, "speedLevel"), SPEEDPOINTS(49, "speedPoints"), STARTINGPOINTS(50, "startPoints"), STONESWORDCOST(51, "stoneSword"), 
	STONESWORDLEVEL(52, "stoneSwordLevel"), TELEPORTTIME(53, "teleportTime"), WOLFLEVELS(54, "wolfLevels"), WOODSWORDCOST(55, "woodSword"), WOODSWORDLEVEL(56, "woodSwordLevel"), 
	JUGGERNAUTPOINTS(57, "juggernautPoints"), JUGGERNAUTLEVEL(58, "juggernautLevel");
	/**@formatter:on**/
	private int id;
	private String setting;
	private Object object;
	private final static Map<Integer, Setting> BY_ID = Maps.newHashMap();

	Setting(int id, String name) {
		this.id = id;
		this.setting = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return setting;
	}

	public void set(Object object) {
		this.object = object;
	}

	public Object getSetting() {
		return object;
	}

	public static int getHighestId() {
		return BY_ID.size();
	}

	public static Setting getById(final int id) {
		return BY_ID.get(id);
	}

	static {
		FileConfiguration config = Ablockalypse.instance.getConfig();
		for (Setting setting : values()) {
			setting.set(config.get(setting.getName()));
			BY_ID.put(setting.id, setting);
		}
	}
}
