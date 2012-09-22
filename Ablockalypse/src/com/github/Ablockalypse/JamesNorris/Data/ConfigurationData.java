package com.github.Ablockalypse.JamesNorris.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import com.github.Ablockalypse.Ablockalypse;

public class ConfigurationData {
	public int buyLevel, woodSwordLevel, stoneSwordLevel, ironSwordLevel, diamondSwordLevel, goldSwordLevel, grenadeLevel;
	public List<Integer> wolfLevels = new ArrayList<Integer>();
	public int cost, enchDamageCost, enchRandomCost, powerrad;
	public boolean DEBUG, ENABLE_AUTO_UPDATE, effects;
	public Enchantment enchant;
	public int heallevel, speedlevel, damagelevel, regenlevel;
	public int healPoints, speedPoints, damagePoints, regenPoints;
	public int powerchance, atompoints;
	public String helmet, chestplate, leggings, boots;
	public List<String> inventory;
	public boolean losePerksLastStand, xmppGameStart, xmppGameEnd, xmppPlayerJoin, xmppPlayerLeave, xmppLastStand;
	public int packapunchlevel;
	public int startpoints, pointincrease, maxplayers = 4, lsthresh, duration = Integer.MAX_VALUE, mccost, helppoints, speedLevel;
	public int woodSwordCost, stoneSwordCost, ironSwordCost, diamondSwordCost, goldSwordCost, grenadeCost;

	/**
	 * Data that is used to load all config values into data used by signs to make changes.
	 * 
	 * @param instance The instance of the plugin Ablockalypse
	 */
	public ConfigurationData(final Ablockalypse plugin) {
		final FileConfiguration cf = plugin.getConfig();
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
		/* XMPP */
		xmppGameStart = cf.getBoolean("xmppAnnounceGameStart");
		xmppGameEnd = cf.getBoolean("xmppAnnounceGameEnd");
		xmppPlayerJoin = cf.getBoolean("xmppAnnouncePlayerJoinGame");
		xmppPlayerLeave = cf.getBoolean("xmppAnnouncePlayerLeaveGame");
		xmppLastStand = cf.getBoolean("xmppAnnounceLastStand");
		/* OTHER */
		lsthresh = cf.getInt("lastStandThreshold");
		losePerksLastStand = cf.getBoolean("losePerksOnLastStand");
		mccost = cf.getInt("mysteryChestCost");
		helppoints = cf.getInt("pointsGivenOnHelp");
		speedLevel = cf.getInt("doubleSpeedLevel");
		wolfLevels = cf.getIntegerList("wolfLevels");
		effects = cf.getBoolean("addedEffects");
		powerrad = cf.getInt("powerupRadius");
		ENABLE_AUTO_UPDATE = cf.getBoolean("ENABLE_AUTO_UPDATE");
		DEBUG = cf.getBoolean("DEBUG");
	}

	// END OF VARIABLES
	/**
	 * Get a random enchantment
	 */
	public Enchantment randomEnchant() {
		final Random rand = new Random();
		final int type = rand.nextInt(3) + 1;
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

	/**
	 * Clears all of the ConfigurationData.
	 */
	@SuppressWarnings("unused") @Override public void finalize() {
		for (Method m : this.getClass().getDeclaredMethods())
			m = null;
		for (Field f : this.getClass().getDeclaredFields())
			f = null;
	}
}
