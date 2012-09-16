package com.github.Ablockalypse.JamesNorris;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import com.github.Ablockalypse.Ablockalypse;

public class ConfigurationData {
	public int buyLevel, woodSwordLevel, stoneSwordLevel, ironSwordLevel, diamondSwordLevel, goldSwordLevel, grenadeLevel;
	public List<Integer> wolfLevels = new ArrayList<Integer>();
	public int cost, enchDamageCost, enchRandomCost;
	public boolean DEBUG;
	public Enchantment enchant;
	public String enchdamagestring, enchrandstring;
	public HashMap<String, Enchantment> enchmap = new HashMap<String, Enchantment>();
	public HashMap<String, Integer> enchsignline3 = new HashMap<String, Integer>();
	public String enchstring;
	public String first, joingame, areastring;
	public int heallevel, speedlevel, damagelevel, regenlevel;
	public int healPoints, speedPoints, damagePoints, regenPoints;
	public String healstring, speedstring, damagestring, regenstring;
	public String helmet, chestplate, leggings, boots;
	public List<String> inventory;
	public HashMap<String, Integer> levelmap = new HashMap<String, Integer>();
	public boolean losePerksLastStand;
	public int packapunchlevel;
	public HashMap<String, PotionEffectType> perkmap = new HashMap<String, PotionEffectType>();
	public HashMap<String, Integer> perksignline3 = new HashMap<String, Integer>();
	public String perkstring;
	private Ablockalypse plugin;
	public int startpoints, pointincrease, maxplayers = 4, lsthresh, duration = Integer.MAX_VALUE, mccost, helppoints, speedLevel;
	public String weaponstring;
	public String weaponwoodstring, weaponstonestring, weaponironstring, weapondiamondstring, weapongoldstring, weapongrenadestring;
	public HashMap<String, Material> wepmap = new HashMap<String, Material>();
	public HashMap<String, Integer> wepsignline3 = new HashMap<String, Integer>();
	public int woodSwordCost, stoneSwordCost, ironSwordCost, diamondSwordCost, goldSwordCost, grenadeCost;

	/**
	 * Data that is used to load all config values into data used by signs to make changes.
	 * 
	 * @param instance The instance of the plugin Ablockalypse
	 */
	public ConfigurationData(Ablockalypse instance) {
		plugin = instance;
		FileConfiguration cf = plugin.getConfig();
		/* STRINGS */
		first = cf.getString("baseString");
		perkstring = cf.getString("perkString");
		healstring = cf.getString("perkHealString");
		speedstring = cf.getString("perkSpeedString");
		damagestring = cf.getString("perkDamageString");
		regenstring = cf.getString("perkRegenString");
		enchstring = cf.getString("enchantmentString");
		enchdamagestring = cf.getString("enchantmentDamageString");
		enchrandstring = cf.getString("enchantmentRandomString");
		weaponstring = cf.getString("weaponString");
		weaponwoodstring = cf.getString("weaponWoodSwordString");
		weaponstonestring = cf.getString("weaponStoneSwordString");
		weaponironstring = cf.getString("weaponIronSwordString");
		weapondiamondstring = cf.getString("weaponDiamondSwordString");
		weapongoldstring = cf.getString("weaponGoldSwordString");
		weapongrenadestring = cf.getString("weaponGrenadeString");
		areastring = cf.getString("areaString");
		joingame = cf.getString("joinString");
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
		/* OTHER */
		lsthresh = cf.getInt("lastStandThreshold");
		losePerksLastStand = cf.getBoolean("losePerksOnLastStand");
		mccost = cf.getInt("mysteryChestCost");
		helppoints = cf.getInt("pointsGivenOnHelp");
		speedLevel = cf.getInt("doubleSpeedLevel");
		wolfLevels = cf.getIntegerList("wolfLevels");
		DEBUG = cf.getBoolean("DEBUG");
		initSignRequirements();
	}

	/**
	 * Used to clean up the amount of code required to add all different perks/enchantments/weapons
	 */
	public void initSignRequirements() {
		// levelmap
		levelmap.put(healstring, heallevel);
		levelmap.put(speedstring, speedlevel);
		levelmap.put(damagestring, damagelevel);
		levelmap.put(regenstring, regenlevel);
		levelmap.put(enchdamagestring, packapunchlevel);
		levelmap.put(enchrandstring, packapunchlevel);
		levelmap.put(weaponwoodstring, woodSwordLevel);
		levelmap.put(weaponstonestring, stoneSwordLevel);
		levelmap.put(weaponironstring, ironSwordLevel);
		levelmap.put(weapondiamondstring, diamondSwordLevel);
		levelmap.put(weapongoldstring, goldSwordLevel);
		levelmap.put(weapongrenadestring, grenadeLevel);
		// perksignline3
		perksignline3.put(healstring, healPoints);
		perksignline3.put(speedstring, speedPoints);
		perksignline3.put(damagestring, damagePoints);
		perksignline3.put(regenstring, regenPoints);
		// perkmap
		perkmap.put(healstring, PotionEffectType.HEAL);
		perkmap.put(speedstring, PotionEffectType.SPEED);
		perkmap.put(damagestring, PotionEffectType.DAMAGE_RESISTANCE);
		perkmap.put(regenstring, PotionEffectType.REGENERATION);
		// enchsignline3
		enchsignline3.put(enchdamagestring, enchDamageCost);
		enchsignline3.put(enchrandstring, enchRandomCost);
		// enchmap
		enchmap.put(enchdamagestring, Enchantment.DAMAGE_ALL);
		// wepsignline3
		wepsignline3.put(weaponwoodstring, woodSwordCost);
		wepsignline3.put(weaponstonestring, stoneSwordCost);
		wepsignline3.put(weaponironstring, ironSwordCost);
		wepsignline3.put(weapondiamondstring, diamondSwordCost);
		wepsignline3.put(weapongoldstring, goldSwordCost);
		wepsignline3.put(weapongrenadestring, grenadeCost);
		// wepmap
		wepmap.put(weaponwoodstring, Material.WOOD_SWORD);
		wepmap.put(weaponstonestring, Material.STONE_SWORD);
		wepmap.put(weaponironstring, Material.IRON_SWORD);
		wepmap.put(weapondiamondstring, Material.DIAMOND_SWORD);
		wepmap.put(weapongoldstring, Material.GOLD_SWORD);
		wepmap.put(weapongrenadestring, Material.ENDER_PEARL);
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
