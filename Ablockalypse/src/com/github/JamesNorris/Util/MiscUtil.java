package com.github.JamesNorris.Util;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Manager.ItemManager;
import com.github.JamesNorris.Util.Enumerated.PowerupType;

/**
 * The class for all utility methods. This class can be used for any miscellaneous needs of the plugin.
 */
public class MiscUtil {
	private static ConfigurationData cd;
	private static Random rand;
	private static ItemManager im = new ItemManager();

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
	 * Drops an item in the direction of the player, then has them pick it up.
	 * 
	 * @param from The location to drop from
	 * @param item The item to drop
	 * @param player The player to drop at
	 * @return The item that is dropped
	 */
	public static Item dropItemAtPlayer(Location from, ItemStack item, final Player player) {
		Item i = from.getWorld().dropItemNaturally(from, item);
		i.setPickupDelay(Integer.MAX_VALUE);
		final ItemStack is = i.getItemStack();
		final Item finali = i;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Ablockalypse.instance, new Runnable() {
			@Override public void run() {
				finali.remove();
				im.giveItem(player, is);
			}
		}, 20);
		return i;
	}

	/**
	 * Checks if the given ItemStack is a sword or not.
	 * 
	 * @param i The ItemStack to check for
	 * @return Whether or not the item is a sword
	 */
	public static boolean isSword(ItemStack i) {
		if (i != null) {
			Material t = i.getType();
			return (i != null && (t == Material.WOOD_SWORD || t == Material.STONE_SWORD || t == Material.IRON_SWORD || t == Material.DIAMOND_SWORD || t == Material.GOLD_SWORD));
		}
		return false;
	}

	/**
	 * Searched for a random powerup.
	 * 
	 * @param zap The player to apply the powerup to
	 * @param cause The entity that originated this event
	 */
	public static void randomPowerup(ZAPlayer zap, Entity cause) {
		if (rand == null)
			rand = new Random();
		if (cd == null)
			cd = External.ym.getConfigurationData();
		int chance = rand.nextInt(100) + 1;
		if (chance <= cd.powerchance) {
			int type = rand.nextInt(4) + 1;
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
				case 4:
					ptype = PowerupType.INSTA_KILL;
				break;
			}
			if (ptype != null)
				zap.givePowerup(ptype, cause);
		}
	}
}
