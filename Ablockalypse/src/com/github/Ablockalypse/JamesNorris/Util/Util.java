package com.github.Ablockalypse.JamesNorris.Util;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayerBase;

public class Util {
	public enum PowerupType {
		ATOM_BOMB, BARRIER_FIX, WEAPON_FIX;
	}

	private static ConfigurationData cd;
	private static Random rand;

	/**
	 * Checks if the entity is a mob accepted by Ablockalypse.
	 * 
	 * @param entity The entity to check for
	 * @return Whether or not the entity is accepted
	 */
	public static boolean isAcceptedMob(Entity entity) {
		return (entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.WOLF);
	}

	/**
	 * Checks if the given ItemStack is a weapon or not.
	 * 
	 * @param i The ItemStack to check for
	 * @return Whether or not the item is a weapon
	 */
	public static boolean isWeapon(ItemStack i) {
		Material t = i.getType();
		return (t == Material.WOOD_SWORD || t == Material.STONE_SWORD || t == Material.IRON_SWORD || t == Material.DIAMOND_SWORD || t == Material.GOLD_SWORD);
	}

	/**
	 * Searched for a random powerup.
	 * 
	 * @param zap The player to apply the powerup to
	 */
	public static void randomPowerup(ZAPlayerBase zap) {
		if (rand == null)
			rand = new Random();
		if (cd == null)
			cd = External.ym.getConfigurationData();
		int chance = rand.nextInt(100) + 1;
		if (chance <= cd.powerchance) {
			int type = rand.nextInt(3) + 1;
			PowerupType ptype = null;
			switch (type) {
				case 1:
					ptype = PowerupType.ATOM_BOMB;
				break;
				case 2:
					ptype = PowerupType.BARRIER_FIX;
				break;
				case 3:
					ptype = PowerupType.WEAPON_FIX;
				break;
			}
			if (ptype != null)
				zap.givePowerup(ptype);
		}
	}
}
