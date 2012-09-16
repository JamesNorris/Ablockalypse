package com.github.Ablockalypse.JamesNorris.Manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import com.github.Ablockalypse.JamesNorris.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.GameData;
import com.github.Ablockalypse.JamesNorris.LocalizationData;

public class YamlManager {
	public HashMap<String, Material> wepmap = new HashMap<String, Material>();
	public HashMap<String, Integer> wepsignline3 = new HashMap<String, Integer>();
	public HashMap<String, PotionEffectType> perkmap = new HashMap<String, PotionEffectType>();
	public HashMap<String, Integer> perksignline3 = new HashMap<String, Integer>();
	public HashMap<String, Integer> levelmap = new HashMap<String, Integer>();
	public HashMap<String, Enchantment> enchmap = new HashMap<String, Enchantment>();
	public HashMap<String, Integer> enchsignline3 = new HashMap<String, Integer>();
	private ConfigurationData cd;
	private LocalizationData ld;
	private GameData gd;

	/**
	 * Creates a new instance of the YamlManager, a manager for all ZA configuration.
	 * 
	 * @param cd The ConfigurationData instance to load to the manager
	 * @param ld The LocalizationData instance to load to the manager
	 */
	public YamlManager(ConfigurationData cd, LocalizationData ld, GameData gd) {
		this.cd = cd;
		this.ld = ld;
		this.gd = gd;
		initSignRequirements();
	}

	/**
	 * Used to clean up the amount of code required to add all different perks/enchantments/weapons.
	 */
	public void initSignRequirements() {
		// levelmap
		levelmap.put(ld.healstring, cd.heallevel);
		levelmap.put(ld.speedstring, cd.speedlevel);
		levelmap.put(ld.damagestring, cd.damagelevel);
		levelmap.put(ld.regenstring, cd.regenlevel);
		levelmap.put(ld.enchdamagestring, cd.packapunchlevel);
		levelmap.put(ld.enchrandstring, cd.packapunchlevel);
		levelmap.put(ld.weaponwoodstring, cd.woodSwordLevel);
		levelmap.put(ld.weaponstonestring, cd.stoneSwordLevel);
		levelmap.put(ld.weaponironstring, cd.ironSwordLevel);
		levelmap.put(ld.weapondiamondstring, cd.diamondSwordLevel);
		levelmap.put(ld.weapongoldstring, cd.goldSwordLevel);
		levelmap.put(ld.weapongrenadestring, cd.grenadeLevel);
		// perksignline3
		perksignline3.put(ld.healstring, cd.healPoints);
		perksignline3.put(ld.speedstring, cd.speedPoints);
		perksignline3.put(ld.damagestring, cd.damagePoints);
		perksignline3.put(ld.regenstring, cd.regenPoints);
		// perkmap
		perkmap.put(ld.healstring, PotionEffectType.HEAL);
		perkmap.put(ld.speedstring, PotionEffectType.SPEED);
		perkmap.put(ld.damagestring, PotionEffectType.DAMAGE_RESISTANCE);
		perkmap.put(ld.regenstring, PotionEffectType.REGENERATION);
		// enchsignline3
		enchsignline3.put(ld.enchdamagestring, cd.enchDamageCost);
		enchsignline3.put(ld.enchrandstring, cd.enchRandomCost);
		// enchmap
		enchmap.put(ld.enchdamagestring, Enchantment.DAMAGE_ALL);
		// wepsignline3
		wepsignline3.put(ld.weaponwoodstring, cd.woodSwordCost);
		wepsignline3.put(ld.weaponstonestring, cd.stoneSwordCost);
		wepsignline3.put(ld.weaponironstring, cd.ironSwordCost);
		wepsignline3.put(ld.weapondiamondstring, cd.diamondSwordCost);
		wepsignline3.put(ld.weapongoldstring, cd.goldSwordCost);
		wepsignline3.put(ld.weapongrenadestring, cd.grenadeCost);
		// wepmap
		wepmap.put(ld.weaponwoodstring, Material.WOOD_SWORD);
		wepmap.put(ld.weaponstonestring, Material.STONE_SWORD);
		wepmap.put(ld.weaponironstring, Material.IRON_SWORD);
		wepmap.put(ld.weapondiamondstring, Material.DIAMOND_SWORD);
		wepmap.put(ld.weapongoldstring, Material.GOLD_SWORD);
		wepmap.put(ld.weapongrenadestring, Material.ENDER_PEARL);
	}
	
	/**
	 * Gets the GameData from this instance.
	 * 
	 * @return The GameData used with this instance
	 */
	public GameData getGameData() {
		return gd;
	}

	/**
	 * Gets the ConfigurationData from this instance.
	 * 
	 * @return The ConfigurationData used with this instance
	 */
	public ConfigurationData getConfigurationData() {
		return cd;
	}

	/**
	 * Gets the LocalizationData from this instance.
	 * 
	 * @return The LocalizationData used with this instance
	 */
	public LocalizationData getLocalizationData() {
		return ld;
	}

	/**
	 * Clears all data from this instance.
	 */
	@SuppressWarnings("unused") @Override public void finalize() {
		for (Method m : this.getClass().getDeclaredMethods())
			m = null;
		for (Field f : this.getClass().getDeclaredFields())
			f = null;
	}
}
