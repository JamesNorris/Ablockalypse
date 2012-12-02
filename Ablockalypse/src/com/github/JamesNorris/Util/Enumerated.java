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
		RED((byte) 14), GREEN((byte) 5), BLUE((byte) 11);
		private final byte data;

		ZAColor(byte data) {
			this.data = data;
		}

		public byte getData() {
			return data;
		}
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
		HEAL(1), SPEED(2), DAMAGE(3), REGENERATE(4);
		private final int id;

		ZAPerk(int id) {
			this.id = id;
		}

		public ZAPerk getById(int id) {
			switch (id) {
				case 1:
					return ZAPerk.HEAL;
				case 2:
					return ZAPerk.SPEED;
				case 3:
					return ZAPerk.DAMAGE;
				case 4:
					return ZAPerk.REGENERATE;
			}
			return null;
		}

		public int byId() {
			return id;
		}
	}

	/**
	 * An enumerated type with the types of sounds that are able to be made by Ablockalypse.
	 */
	public enum ZASound {
		ACHIEVEMENT, DEATH, END, LAST_STAND, NEXT_LEVEL, PREV_LEVEL, START, TELEPORT, BARRIER_BREAK, BARRIER_REPAIR, AREA_BUY, AREA_REPLACE, EXPLOSION;
	}
}
