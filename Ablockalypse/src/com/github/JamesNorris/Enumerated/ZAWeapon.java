package com.github.JamesNorris.Enumerated;

import java.util.Map;

import org.bukkit.Material;
import com.google.common.collect.Maps;

public enum ZAWeapon {
	/**@formatter:off**/
	WOOD_SWORD(1, Material.WOOD_SWORD, Local.WEAPONWOODSTRING.getSetting(), (Integer) Setting.WOODSWORDCOST.getSetting(), (Integer) Setting.WOODSWORDLEVEL.getSetting()),
	STONE_SWORD(1, Material.STONE_SWORD, Local.WEAPONSTONESTRING.getSetting(), (Integer) Setting.STONESWORDCOST.getSetting(), (Integer) Setting.STONESWORDLEVEL.getSetting()),
	IRON_SWORD(1, Material.IRON_SWORD, Local.WEAPONIRONSTRING.getSetting(), (Integer) Setting.IRONSWORDCOST.getSetting(), (Integer) Setting.IRONSWORDLEVEL.getSetting()),
	DIAMOND_SWORD(1, Material.DIAMOND_SWORD, Local.WEAPONDIAMONDSTRING.getSetting(), (Integer) Setting.DIAMONDSWORDCOST.getSetting(), (Integer) Setting.DIAMONDSWORDLEVEL.getSetting()),
	GOLD_SWORD(1, Material.GOLD_SWORD, Local.WEAPONGOLDSTRING.getSetting(), (Integer) Setting.GOLDSWORDCOST.getSetting(), (Integer) Setting.GOLDSWORDLEVEL.getSetting()),
	GRENADE(1, Material.ENDER_PEARL, Local.WEAPONGRENADESTRING.getSetting(), (Integer) Setting.GRENADECOST.getSetting(), (Integer) Setting.GRENADELEVEL.getSetting()),
	BOW(1, Material.BOW, "<>", 0, 0);
	/**@formatter:on**/
	private int id, cost, level;
	private String label;
	private Material type;
	private final static Map<Integer, ZAWeapon> BY_ID = Maps.newHashMap();

	ZAWeapon(int id, Material type, String label, int cost, int level) {
		this.id = id;
		this.type = type;
		this.label = label;
		this.cost = cost;
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public Material getMaterial() {
		return type;
	}

	public int getCost() {
		return cost;
	}

	public String getLabel() {
		return label;
	}

	public int getId() {
		return id;
	}

	public static ZAWeapon getById(final int id) {
		return BY_ID.get(id);
	}

	static {
		for (ZAWeapon setting : values())
			BY_ID.put(setting.id, setting);
	}
}
