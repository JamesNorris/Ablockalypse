package com.github.Ablockalypse.JamesNorris;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.Ablockalypse.JamesNorris.Util.External;

public class LocalizationData {
	public String first, perkstring, healstring, speedstring, damagestring, regenstring, enchstring, enchdamagestring;
	public String enchrandstring, weaponstring, weaponwoodstring, weaponstonestring, weaponironstring, weapondiamondstring;
	public String weapongoldstring, weapongrenadestring, areastring, joingame;

	public LocalizationData() {
		FileConfiguration c = External.getConfig(External.l, External.local);
		first = c.getString("baseString");
		perkstring = c.getString("perkString");
		healstring = c.getString("perkHealString");
		speedstring = c.getString("perkSpeedString");
		damagestring = c.getString("perkDamageString");
		regenstring = c.getString("perkRegenString");
		enchstring = c.getString("enchantmentString");
		enchdamagestring = c.getString("enchantmentDamageString");
		enchrandstring = c.getString("enchantmentRandomString");
		weaponstring = c.getString("weaponString");
		weaponwoodstring = c.getString("weaponWoodSwordString");
		weaponstonestring = c.getString("weaponStoneSwordString");
		weaponironstring = c.getString("weaponIronSwordString");
		weapondiamondstring = c.getString("weaponDiamondSwordString");
		weapongoldstring = c.getString("weaponGoldSwordString");
		weapongrenadestring = c.getString("weaponGrenadeString");
		areastring = c.getString("areaString");
		joingame = c.getString("joinString");
	}

	/**
	 * Clears all of the LocalizationData.
	 */
	@SuppressWarnings("unused") @Override public void finalize() {
		for (Method m : this.getClass().getDeclaredMethods())
			m = null;
		for (Field f : this.getClass().getDeclaredFields())
			f = null;
	}
}
