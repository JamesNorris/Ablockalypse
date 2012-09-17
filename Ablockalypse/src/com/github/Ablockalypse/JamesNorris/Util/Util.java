package com.github.Ablockalypse.JamesNorris.Util;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;

public class Util {
	private static Random rand;
	private static ConfigurationData cd;

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

	public static void randomPowerup(ZAPlayer zap) {
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
