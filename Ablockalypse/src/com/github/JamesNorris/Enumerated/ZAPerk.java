package com.github.JamesNorris.Enumerated;

import java.util.Map;

import com.google.common.collect.Maps;

public enum ZAPerk {
	/**@formatter:off**/
	HEAL(1, Local.PERKHEALSTRING.getSetting(), 10, (Integer) Setting.HEALPOINTS.getSetting(), (Integer) Setting.HEALLEVEL.getSetting()), 
	SPEED(2, Local.PERKSPEEDSTRING.getSetting(), (Integer) Setting.PERKDURATION.getSetting(), (Integer) Setting.SPEEDPOINTS.getSetting(), (Integer) Setting.SPEEDLEVEL.getSetting()), 
	DAMAGE(3, Local.PERKDAMAGESTRING.getSetting(), (Integer) Setting.PERKDURATION.getSetting(), (Integer) Setting.DAMAGEPOINTS.getSetting(), (Integer) Setting.DAMAGELEVEL.getSetting()), 
	REGENERATE(4, Local.PERKREGENERATIONSTRING.getSetting(), (Integer) Setting.PERKDURATION.getSetting(), (Integer) Setting.REGENERATIONPOINTS.getSetting(), (Integer) Setting.REGENERATIONLEVEL.getSetting()),
	JUGGERNAUT(5, Local.PERKJUGGERNAUTSTRING.getSetting(), (Integer) Setting.PERKDURATION.getSetting(), (Integer) Setting.JUGGERNAUTPOINTS.getSetting(), (Integer) Setting.JUGGERNAUTLEVEL.getSetting());
	/**@formatter:on**/
	private int id, duration, cost, level;
	private String label;
	private final static Map<Integer, ZAPerk> BY_ID = Maps.newHashMap();

	ZAPerk(int id, String label, int duration, int cost, int level) {
		this.id = id;
		this.label = label;
		this.duration = duration;
		this.cost = cost;
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public int getCost() {
		return cost;
	}

	public int getDuration() {
		return duration;
	}

	public String getLabel() {
		return label;
	}

	public int getId() {
		return id;
	}

	public static ZAPerk getById(final int id) {
		return BY_ID.get(id);
	}

	static {
		for (ZAPerk setting : values())
			BY_ID.put(setting.id, setting);
	}
}
