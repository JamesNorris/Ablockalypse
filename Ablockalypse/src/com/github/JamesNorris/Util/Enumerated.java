package com.github.JamesNorris.Util;

public class Enumerated {
	/**
	 * An enumerated type with the types of mobs use by Ablockalypse.
	 */
	public enum GameEntityType {
		HELLHOUND, UNDEAD;
	}

	/**
	 * An enumerated type with the types of status a player can have.
	 */
	public enum PlayerStatus {
		LAST_STAND, LIMBO, TELEPORTING;
	}

	/**
	 * An enumerated type with the types of powerups available to players.
	 */
	public enum PowerupType {
		ATOM_BOMB, BARRIER_FIX, WEAPON_FIX, INSTA_KILL;
	}

	/**
	 * An enumerated type used for blinker colors.
	 */
	public enum ZAColor {
		RED, GREEN, BLUE;
	}

	/**
	 * An enumerated type for effects in the game.
	 */
	public enum ZAEffect {
		SMOKE, FLAMES, POTION_BREAK, EXTINGUISH, WOOD_BREAK, IRON_BREAK, TELEPORTATION, LIGHTNING, BEACON;
	}

	/**
	 * An enumerated type used for storing perk data.
	 */
	public enum ZAPerk {
		HEAL, SPEED, DAMAGE, REGENERATE;
	}

	/**
	 * An enumerated type with the types of sounds that are able to be made by Ablockalypse.
	 */
	public enum ZASound {
		ACHIEVEMENT, DEATH, END, LAST_STAND, NEXT_LEVEL, PREV_LEVEL, START, TELEPORT, BARRIER_BREAK, BARRIER_REPAIR, AREA_BUY, AREA_REPLACE, EXPLOSION;
	}
}
