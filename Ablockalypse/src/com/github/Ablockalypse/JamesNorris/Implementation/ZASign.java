package com.github.Ablockalypse.JamesNorris.Implementation;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.Ablockalypse.JamesNorris.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data;
import com.github.Ablockalypse.JamesNorris.Interface.ZASignInterface;

public class ZASign implements ZASignInterface {
	private ConfigurationData cd;
	private String l1, l2, l3, l4;
	private Sign sign;
	private World world;
	private int x, y, z;

	/**
	 * Creates a new instance of a ZASign.
	 * 
	 * @param sign The sign to be made into this instance
	 * @param cd The ConfigurationData instance to be used in this instance
	 */
	public ZASign(Sign sign, ConfigurationData cd) {
		this.sign = sign;
		this.cd = cd;
		this.l1 = sign.getLine(1);
		this.l2 = sign.getLine(2);
		this.l3 = sign.getLine(3);
		this.l4 = sign.getLine(4);
		this.x = sign.getX();
		this.y = sign.getY();
		this.z = sign.getZ();
		this.world = sign.getWorld();
	}

	/**
	 * Gets the specified line 1-4.
	 * 
	 * @param number The line number to get, 1-4
	 * @return The string of the line specified
	 */
	@Override public String getLine(int number) {
		switch (number) {
			case 1:
				return l1;
			case 2:
				return l2;
			case 3:
				return l3;
			case 4:
				return l4;
		}
		return null;
	}

	/**
	 * Gets the sign this instance is attached to.
	 * 
	 * @return The sign this instance is attached to
	 */
	@Override public Sign getSign() {
		return sign;
	}

	/**
	 * Gets the location of the sign.
	 * 
	 * @return The location of the sign
	 */
	@Override public Location getLocation() {
		return new Location(world, x, y, z);
	}

	/**
	 * Gets the world this sign is located in.
	 * 
	 * @return The world this sign is located in
	 */
	@Override public World getWorld() {
		return world;
	}

	/**
	 * Checks the lines of the sign for strings in the config, that enable changes to be made to the player.
	 * 
	 * @param player The player to affect if the lines are run through
	 */
	@Override public void runLines(Player player) {
		/* Makes sure the sign has the first requirement to be a ZA sign */
		if (l1.equalsIgnoreCase(cd.first)) {
			/* Attempts to add the player to a game if the second line has the join string */
			if (l2.equalsIgnoreCase(cd.joingame)) {
				ZAPlayer zap = Data.findZAPlayer(player, l3);
				zap.loadPlayerToGame(l3);
				/* Otherwise, checks for enough points, then attempts to purchase something for the player */
			} else if (player.getLevel() >= cd.levelmap.get(l3) && Data.players.containsKey(player)) {
				ZAPlayer zap = Data.players.get(player);
				int n = zap.getPoints();
				/* PERKS */
				if (l2.equalsIgnoreCase(cd.perkstring)) {
					if (cd.perksignline3.containsKey(l3) && n >= cd.perksignline3.get(l3)) {
						if (cd.perkmap.get(l3) == PotionEffectType.HEAL) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 5, 5));
						} else {
							player.addPotionEffect(new PotionEffect(cd.perkmap.get(l3), cd.duration, 2));
						}
						player.sendMessage(ChatColor.BOLD + "You have bought the " + l3 + " perk for " + cd.perksignline3.get(l3) + " points!");
						return;
					} else {
						player.sendMessage(ChatColor.RED + "You don't have enough points for this!");
						return;
					}
					/* ENCHANTMENTS */
				} else if (l2.equalsIgnoreCase(cd.enchstring)) {
					if (cd.enchsignline3.containsKey(l3) && n >= cd.enchsignline3.get(l3)) {
						if (l3.equalsIgnoreCase(cd.enchrandstring)) {
							player.getItemInHand().addEnchantment(cd.randomEnchant(), 3);
							player.sendMessage(ChatColor.BOLD + "You have bought a " + l3 + " enchantment for " + cd.enchsignline3.get(l3) + " points!");
							return;
						} else {
							player.getItemInHand().addEnchantment(cd.enchmap.get(l3), 3);
						}
						player.sendMessage(ChatColor.BOLD + "You have bought the " + l3 + " enchantment for " + cd.enchsignline3.get(l3) + " points!");
						return;
					} else {
						player.sendMessage(ChatColor.RED + "You don't have enough points for this!");
						return;
					}
					/* WEAPONS */
				} else if (l2.equalsIgnoreCase(cd.weaponstring)) {
					if (cd.wepsignline3.containsKey(l3) && n >= cd.enchsignline3.get(l3)) {
						player.getInventory().addItem(new ItemStack(cd.wepmap.get(l3), 1));
						player.sendMessage(ChatColor.BOLD + "You have bought a " + l3 + " for " + cd.enchsignline3.get(l3) + " points!");
						return;
					} else {
						player.sendMessage(ChatColor.RED + "You don't have enough points for this!");
						return;
					}
					/* AREAS */
				} else if (l2.equalsIgnoreCase(cd.areastring)) {
					Block b = sign.getBlock();
					Area a;
					if (!Data.areas.containsKey(b))
						a = Data.areas.get(b);
					else
						a = new Area(b);
					a.purchaseArea();
				} else {
					System.err.println(ChatColor.RED + "The sign at: [" + world + " : " + x + "," + y + "," + "z" + "] is incorrectly formatted!");
				}
			}
		}
	}
}
