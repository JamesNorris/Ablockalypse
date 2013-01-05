package com.github.JamesNorris.Enumerated;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.JamesNorris.External;
import com.google.common.collect.Maps;

public enum Local {
	/**@formatter:off**/
	ENCHANTMENTRANDOMSTRING(1, "enchantmentRandomString"), BASEWEAPONSTRING(2, "weaponString"), WEAPONWOODSTRING(3, "weaponWoodSwordString"), WEAPONSTONESTRING(4, "weaponStoneSwordString"), WEAPONIRONSTRING(5, "weaponIronSwordString"),
	WEAPONDIAMONDSTRING(6, "weaponDiamondSwordString"), BASESTRING(7, "baseString"), BASEPERKSTRING(8, "perkString"), PERKHEALSTRING(9, "perkHealString"), PERKSPEEDSTRING(10, "perkSpeedString"), PERKDAMAGESTRING(11, "perkDamageString"),
	PERKREGENERATIONSTRING(11, "perkRegenerationString"), WEAPONGOLDSTRING(12, "weaponGoldSwordString"), WEAPONGRENADESTRING(13, "weaponGrenadeString"), BASEAREASTRING(14, "areaString"), BASEJOINSTRING(15, "joinString"), NAMEDWOODSWORD(16, "woodSword"),
	NAMEDSTONESWORD(17, "stoneSword"), NAMEDIRONSWORD(18, "ironSword"), NAMEDDIAMONDSWORD(19, "diamondSword"), NAMEDGOLDSWORD(20, "goldSword"), NAMEDBOW(21, "bow"), BASEENCHANTMENTSTRING(22, "enchantmentString"),
	ENCHANTMENTDAMAGESTRING(23, "enchantmentDamageString"), PERKJUGGERNAUTSTRING(24, "perkJuggernautString");
	/**@formatter:on**/
	private int id;
	private String setting, object;
	private final static Map<Integer, Local> BY_ID = Maps.newHashMap();

	Local(int id, String setting) {
		this.id = id;
		this.setting = setting;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return setting;
	}

	public void set(String object) {
		this.object = object;
	}

	public String getSetting() {
		return object;
	}

	public static int getHighestId() {
		return BY_ID.size();
	}

	public static Local getById(final int id) {
		return BY_ID.get(id);
	}

	static {
		FileConfiguration local = External.getConfig(External.localizationFile, External.local);
		for (Local setting : values()) {
			setting.set(local.getString(setting.getName()));
			BY_ID.put(setting.id, setting);
		}
	}
}
