package com.github.JamesNorris.Data;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.JamesNorris.External;

public class LocalizationData {
	public String enchrandstring, weaponstring, weaponwoodstring, weaponstonestring, weaponironstring, weapondiamondstring;
	public String first, perkstring, healstring, speedstring, damagestring, regenstring, enchstring, enchdamagestring;
	public String weapongoldstring, weapongrenadestring, areastring, joingame, woodsword, stonesword, ironsword, goldsword;
	public String diamondsword, bow;

	public LocalizationData(File file, String path) {
		FileConfiguration c = External.getConfig(file, path);
		first = c.getString("baseString");
		perkstring = c.getString("perkString");
		healstring = c.getString("perkHealString");
		speedstring = c.getString("perkSpeedString");
		damagestring = c.getString("perkDamageString");
		regenstring = c.getString("perkRegenerationString");
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
		woodsword = c.getString("woodSword");
		stonesword = c.getString("stoneSword");
		ironsword = c.getString("ironSword");
		goldsword = c.getString("goldSword");
		diamondsword = c.getString("diamondSword");
		bow = c.getString("bow");
	}
}
