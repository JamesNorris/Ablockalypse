package com.github.JamesNorris.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;

public class ConfigurationData {
	// TODO wish I could find a way to organize these with eclipse...
	public int atompoints;
	public int barrierfullfix;
	public int barrierpartfix;
	public boolean beacons;
	public boolean blinkers;
	public boolean xmppGameStart;
	public boolean xmppGameEnd;
	public boolean xmppPlayerJoin;
	public boolean xmppPlayerLeave;
	public boolean xmppLastStand;
	public boolean clearmobs;
	public int damagelevel;
	public int damagePoints;
	public boolean DEBUG;
	public int diamondSwordCost;
	public int diamondSwordLevel;
	public int doubleSpeedLevel;
	public int duration = Integer.MAX_VALUE;
	public boolean ENABLE_AUTO_UPDATE;
	public int enchDamageCost;
	public int enchRandomCost;
	public boolean extraEffects;
	public boolean defaultFF;
	public int goldSwordCost;
	public int goldSwordLevel;
	public int grenadeCost;
	public int grenadeLevel;
	public int heallevel;
	public int healPoints;
	public String helmet;
    public String chestplate;
    public String leggings;
    public String boots;
	public int helppoints;
	public List<String> inventory;
	public int ironSwordCost;
	public int ironSwordLevel;
	public boolean losePerksLastStand;
	public int lsthresh;
	public int maxplayers;
	public int mccost;
	public boolean movingchests;
	public int packapunchlevel;
	public int pointincrease;
	public int powerchance;
	public int regenlevel;
	public int regenPoints;
	public int speedlevel;
	public int speedPoints;
	public int startpoints;
	public int stoneSwordCost;
	public int stoneSwordLevel;
	public int teleportTime;
	public List<Integer> wolfLevels = new ArrayList<Integer>();
	public int woodSwordCost;
	public int woodSwordLevel;

	/**
	 * Data that is used to load all config values into data used by signs to make changes.
	 * 
	 * @param instance The instance of the plugin Ablockalypse
	 */
	public ConfigurationData(Plugin plugin) {
		FileConfiguration cf = plugin.getConfig();
		/* POINTS & LEVELS */
		heallevel = cf.getInt("healLevel");
		speedlevel = cf.getInt("speedLevel");
		damagelevel = cf.getInt("damageLevel");
		regenlevel = cf.getInt("regenLevel");
		healPoints = cf.getInt("healPoints");
		speedPoints = cf.getInt("speedPoints");
		damagePoints = cf.getInt("damagePoints");
		regenPoints = cf.getInt("regenPoints");
		packapunchlevel = cf.getInt("packapunchLevel");
		enchDamageCost = cf.getInt("enchDamageCost");
		enchRandomCost = cf.getInt("enchRandomCost");
		woodSwordLevel = cf.getInt("woodSwordLevel");
		stoneSwordLevel = cf.getInt("stoneSwordLevel");
		ironSwordLevel = cf.getInt("ironSwordLevel");
		diamondSwordLevel = cf.getInt("diamondSwordLevel");
		goldSwordLevel = cf.getInt("goldSwordLevel");
		grenadeLevel = cf.getInt("grenadeLevel");
		woodSwordCost = cf.getInt("woodSword");
		stoneSwordCost = cf.getInt("stoneSword");
		ironSwordCost = cf.getInt("ironSword");
		diamondSwordCost = cf.getInt("diamondSword");
		goldSwordCost = cf.getInt("goldSword");
		grenadeCost = cf.getInt("perGrenade");
		startpoints = cf.getInt("startPoints");
		pointincrease = cf.getInt("pointIncrease");
		inventory = cf.getStringList("startingItems.inventory");
		helmet = cf.getString("startingItems.armor.helmet");
		chestplate = cf.getString("startingItems.armor.chestplate");
		leggings = cf.getString("startingItems.armor.leggings");
		boots = cf.getString("startingItems.armor.boots");
		powerchance = cf.getInt("powerupChance");
		atompoints = cf.getInt("pointsGivenOnAtomBomb");
		barrierfullfix = cf.getInt("barrierCompleteFixIncrease");
		barrierpartfix = cf.getInt("barrierPerFixIncrease");
		mccost = cf.getInt("mysteryChestCost");
		helppoints = cf.getInt("pointsGivenOnHelp");
		/* XMPP */
		xmppGameStart = cf.getBoolean("xmppAnnounceGameStart");
		xmppGameEnd = cf.getBoolean("xmppAnnounceGameEnd");
		xmppPlayerJoin = cf.getBoolean("xmppAnnouncePlayerJoinGame");
		xmppPlayerLeave = cf.getBoolean("xmppAnnouncePlayerLeaveGame");
		xmppLastStand = cf.getBoolean("xmppAnnounceLastStand");
		/* OTHER */
		defaultFF = cf.getBoolean("defaultFriendlyFireMode");
		blinkers = cf.getBoolean("blinkers");
		lsthresh = cf.getInt("lastStandThreshold");
		losePerksLastStand = cf.getBoolean("losePerksOnLastStand");
		doubleSpeedLevel = cf.getInt("doubleSpeedLevel");
		wolfLevels = cf.getIntegerList("wolfLevels");
		extraEffects = cf.getBoolean("addedEffects");
		clearmobs = cf.getBoolean("clearNearbyMobs");
		teleportTime = cf.getInt("teleportTime");
		movingchests = cf.getBoolean("movingChests");
		maxplayers = cf.getInt("maxPlayers");
		beacons = cf.getBoolean("chestBeacons");
		ENABLE_AUTO_UPDATE = cf.getBoolean("ENABLE_AUTO_UPDATE");
		DEBUG = cf.getBoolean("DEBUG");
	}

	// END OF VARIABLES
	/**
	 * Get a random enchantment
	 */
	public Enchantment randomEnchant() {
		Random rand = new Random();
		int type = rand.nextInt(3) + 1;
		switch (type) {
			case 1:
				return Enchantment.DAMAGE_ALL;
			case 2:
				return Enchantment.FIRE_ASPECT;
			case 3:
				return Enchantment.KNOCKBACK;
		}
		return Enchantment.DURABILITY;
	}
}
