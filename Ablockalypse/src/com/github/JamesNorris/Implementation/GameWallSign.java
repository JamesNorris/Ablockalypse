package com.github.JamesNorris.Implementation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Event.GameCreateEvent;
import com.github.JamesNorris.Event.GameSignClickEvent;
import com.github.JamesNorris.Interface.WallSign;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Manager.YamlManager;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.EffectUtil.ZAEffect;
import com.github.JamesNorris.Util.MiscUtil;

public class GameWallSign implements WallSign {
	private ConfigurationData cd;
	private String l1, l2, l3, l4;
	private LocalizationData ld;
	private Sign sign;
	private World world;
	private int x, y, z;
	private YamlManager ym;

	/**
	 * Creates a new instance of a ZASign.
	 * 
	 * @param sign The sign to be made into this instance
	 * @param cd The ConfigurationData instance to be used in this instance
	 */
	public GameWallSign(Sign sign, YamlManager ym) {
		this.sign = sign;
		ld = ym.getLocalizationData();
		cd = ym.getConfigurationData();
		this.ym = ym;
		l1 = sign.getLine(0);
		l2 = sign.getLine(1);
		l3 = sign.getLine(2);
		l4 = sign.getLine(3);
		x = sign.getX();
		y = sign.getY();
		z = sign.getZ();
		world = sign.getWorld();
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
	 * Gets the location of the sign.
	 * 
	 * @return The location of the sign
	 */
	@Override public Location getLocation() {
		return new Location(world, x, y, z);
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
	@SuppressWarnings("deprecation") @Override public void runLines(Player player) {
		/* Makes sure the sign has the first requirement to be a ZA sign */
		if (l1.equalsIgnoreCase(ld.first)) {
			GameSignClickEvent gsce = new GameSignClickEvent(sign);
			Bukkit.getPluginManager().callEvent(gsce);
			if (!gsce.isCancelled())
				/* Attempts to add the player to a game if the second line has the join string */
				if (l2.equalsIgnoreCase(ld.joingame) && !Data.players.containsKey(player)) {
					if (player.hasPermission("za.create") && !Data.games.containsKey(l3)) {
						setupPlayerWithGame(l3, player, true);
						return;
					} else if (Data.games.containsKey(l3)) {
						setupPlayerWithGame(l3, player, false);
						EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
						return;
					} else {
						player.sendMessage(ChatColor.RED + "That game does not exist!");
						return;
					}
					/* Otherwise, checks for enough points, then attempts to purchase something for the player */
				} else if (!l2.equalsIgnoreCase(ld.joingame) && l3 != null && ym.levelmap.get(l3) != null && ym.levelmap != null && Data.players != null && Data.players.containsKey(player)) {
					ZAPlayerBase zap = Data.players.get(player);
					int n = zap.getPoints();
					if (player.getLevel() >= ym.levelmap.get(l3)) {
						/* PERKS */
						if (l2.equalsIgnoreCase(ld.perkstring)) {
							if (ym.perksignline3.containsKey(l3) && n >= ym.perksignline3.get(l3)) {
								if (ym.perkmap.get(l3) == PotionEffectType.HEAL)
									player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 5, 5));
								else
									player.addPotionEffect(new PotionEffect(ym.perkmap.get(l3), cd.duration, 2));
								int cost = ym.perksignline3.get(l3);
								zap.subtractPoints(cost);
								player.sendMessage(ChatColor.BOLD + "You have bought the " + l3 + " perk for " + cost + " points!");
								EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
								return;
							} else {
								player.sendMessage(ChatColor.RED + "You need " + ym.perksignline3.get(l3) + " points to buy this. You currently have " + n);
								return;
							}
							/* ENCHANTMENTS */
						} else if (l2.equalsIgnoreCase(ld.enchstring) && MiscUtil.isWeapon(player.getItemInHand())) {
							if (ym.enchsignline3.containsKey(l3) && n >= ym.enchsignline3.get(l3)) {
								if (l3.equalsIgnoreCase(ld.enchrandstring))
									player.getItemInHand().addEnchantment(cd.randomEnchant(), 3);
								else
									player.getItemInHand().addEnchantment(ym.enchmap.get(l3), 3);
								int cost = ym.enchsignline3.get(l3);
								zap.subtractPoints(cost);
								player.sendMessage(ChatColor.BOLD + "You have bought the " + l3 + " enchantment for " + cost + " points!");
								EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
								return;
							} else {
								player.sendMessage(ChatColor.RED + "You need " + ym.enchsignline3.get(l3) + " points to buy this. You currently have " + n);
								return;
							}
							/* WEAPONS */
						} else if (l2.equalsIgnoreCase(ld.weaponstring)) {
							if (ym.wepsignline3.containsKey(l3) && n >= ym.wepsignline3.get(l3)) {
								if (ym.wepmap.get(l3) != Material.ENDER_PEARL)
									player.getInventory().addItem(new ItemStack(ym.wepmap.get(l3), 1));
								else
									player.getInventory().addItem(new ItemStack(ym.wepmap.get(l3), 5));
								int cost = ym.wepsignline3.get(l3);
								zap.subtractPoints(cost);
								player.sendMessage(ChatColor.BOLD + "You have bought a " + l3 + " for " + cost + " points!");
								EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
								return;
							} else {
								player.sendMessage(ChatColor.RED + "You need " + ym.wepsignline3.get(l3) + " points to buy this. You currently have " + n);
								return;
							}
							/* AREAS */
						} else if (l2.equalsIgnoreCase(ld.areastring))
							try {
								int cost = Integer.parseInt(l3);
								if (zap.getPoints() >= cost) {
									Block b = sign.getBlock();
									GameArea a;
									if (!Data.areas.containsKey(b))
										a = Data.areas.get(b);
									else
										a = new GameArea(b);
									if (!a.isPurchased()) {
										a.purchaseArea();
										EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
										zap.subtractPoints(cost);
										player.sendMessage(ChatColor.BOLD + "You have bought an area for " + cost + " points.");
										return;
									} else
										player.sendMessage(ChatColor.RED + "This area has already been purchased!");
									return;
								} else {
									player.sendMessage(ChatColor.RED + "You need " + cost + " points to buy this. You currently have " + zap.getPoints());
									return;
								}
							} catch (Exception e) {
								return;
							}
						else
							return;
					} else {
						player.sendMessage(ChatColor.RED + "Level " + ym.levelmap.get(l3) + " is required to buy that. Your current level is " + player.getLevel());
						return;
					}
				} else
					return;
			player.updateInventory();
		}
	}

	/*
	 * Checks for the game and player to create a new game instance and player instance.
	 */
	private void setupPlayerWithGame(String name, Player player, boolean created) {
		ZAGame zag = Data.findGame(l3);
		if (created)
			zag.setSpawn(player.getLocation());// TODO remove this when we have a proper spawn-setting system
		ZAPlayer zap = Data.findZAPlayer(player, l3);
		GameCreateEvent gce = new GameCreateEvent(zag, null, player);
		Bukkit.getServer().getPluginManager().callEvent(gce);
		if (!gce.isCancelled())
			zap.loadPlayerToGame(l3);
		else
			zag.endGame();
	}
}
