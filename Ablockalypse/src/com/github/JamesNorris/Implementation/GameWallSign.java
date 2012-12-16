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
import org.bukkit.potion.PotionEffectType;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Event.GameCreateEvent;
import com.github.JamesNorris.Event.GameSignClickEvent;
import com.github.JamesNorris.Interface.WallSign;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Manager.ItemManager;
import com.github.JamesNorris.Manager.YamlManager;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;
import com.github.JamesNorris.Util.Enumerated.ZAPerk;
import com.github.JamesNorris.Util.MathAssist;
import com.github.JamesNorris.Util.MiscUtil;

public class GameWallSign extends DataManipulator implements WallSign {
	private String l1, l2, l3, l4;
	private Sign sign;
	private World world;
	private int x, y, z;
	private ItemManager im;

	/**
	 * Creates a new instance of a ZASign.
	 * 
	 * @param sign The sign to be made into this instance
	 * @param cd The ConfigurationData instance to be used in this instance
	 */
	public GameWallSign(Sign sign, YamlManager ym) {
		this.sign = sign;
		im = new ItemManager();
		ld = ym.getLocalizationData();
		cd = ym.getConfigurationData();
		l1 = sign.getLine(0);
		l2 = sign.getLine(1);
		l3 = sign.getLine(2);
		l4 = sign.getLine(3);
		x = sign.getX();
		y = sign.getY();
		z = sign.getZ();
		world = sign.getWorld();
	}

	/*
	 * Gets the closest game area to the given block.
	 */
	private GameArea getClosestArea(Block b, ZAGameBase zag) {
		int distance = Integer.MAX_VALUE;
		Location loc = b.getLocation();
		GameArea lp = null;
		for (GameArea a : data.areas)
			if (a.getGame() == zag) {
				Location l = a.getPoint(1);
				int current = (int) MathAssist.distance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
				if (current < distance) {
					distance = current;
					lp = a;
				}
			}
		if (lp != null)
			return lp;
		return null;
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
				if (l2.equalsIgnoreCase(ld.joingame) && !data.players.containsKey(player)) {
					if (player.hasPermission("za.create") && !data.games.containsKey(l3)) {
						setupPlayerWithGame(l3, player);
						player.sendMessage(ChatColor.RED + "This game does not have any barriers. Ignoring...");
						return;
					} else if (data.games.containsKey(l3)) {
						setupPlayerWithGame(l3, player);
						EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
						return;
					} else {
						player.sendMessage(ChatColor.RED + "That game does not exist!");
						return;
					}
					/* Otherwise, checks for enough points, then attempts to purchase something for the player */
				} else if (!l2.equalsIgnoreCase(ld.joingame) && l3 != null && ym.levelmap != null && data.players != null && data.players.containsKey(player)) {
					ZAPlayerBase zap = data.players.get(player);
					int n = zap.getPoints();
					if (ym.levelmap.get(l3) == null || player.getLevel() >= ym.levelmap.get(l3)) {
						/* PERKS */
						if (l2.equalsIgnoreCase(ld.perkstring)) {
							if (ym.perksignline3.containsKey(l3) && n >= ym.perksignline3.get(l3)) {
								if (ym.perkmap.get(l3) == PotionEffectType.HEAL)
									zap.addPerk(ZAPerk.HEAL, 5, 5);
								else {
									if (l3.equalsIgnoreCase(ld.speedstring))
										zap.addPerk(ZAPerk.SPEED, cd.duration, 1);
									if (l3.equalsIgnoreCase(ld.damagestring))
										zap.addPerk(ZAPerk.DAMAGE, cd.duration, 1);
									if (l3.equalsIgnoreCase(ld.regenstring))
										zap.addPerk(ZAPerk.REGENERATE, cd.duration, 1);
								}
								int cost = ym.perksignline3.get(l3);
								zap.subtractPoints(cost);
								player.sendMessage(ChatColor.BOLD + "You have bought " + l3 + " for " + cost + " points!");
								EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
								return;
							} else {
								player.sendMessage(ChatColor.RED + "You have " + n + " / " + ym.perksignline3.get(l3) + " points to buy this.");
								return;
							}
							/* ENCHANTMENTS */
						} else if (l2.equalsIgnoreCase(ld.enchstring) && MiscUtil.isSword(player.getItemInHand())) {
							if (ym.enchsignline3.containsKey(l3) && n >= ym.enchsignline3.get(l3)) {
								ItemStack hand = player.getItemInHand();
								player.getInventory().remove(hand);
								if (l3.equalsIgnoreCase(ld.enchrandstring))
									im.addEnchantment(hand, cd.randomEnchant(), 3);
								else
									im.addEnchantment(hand, ym.enchmap.get(l3), 3);
								int cost = ym.enchsignline3.get(l3);
								zap.subtractPoints(cost);
								player.sendMessage(ChatColor.BOLD + "You have bought " + l3 + " for " + cost + " points!");
								EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
								MiscUtil.dropItemAtPlayer(sign.getLocation(), hand, player);
								return;
							} else {
								player.sendMessage(ChatColor.RED + "You have " + n + " / " + ym.enchsignline3.get(l3) + " points to buy this.");
								return;
							}
							/* WEAPONS */
						} else if (l2.equalsIgnoreCase(ld.weaponstring)) {
							if (ym.wepsignline3.containsKey(l3) && n >= ym.wepsignline3.get(l3)) {
								if (ym.wepmap.get(l3) != Material.ENDER_PEARL)
									MiscUtil.dropItemAtPlayer(sign.getLocation(), new ItemStack(ym.wepmap.get(l3), 1), player);
								else
									MiscUtil.dropItemAtPlayer(sign.getLocation(), new ItemStack(ym.wepmap.get(l3), 5), player);
								int cost = ym.wepsignline3.get(l3);
								zap.subtractPoints(cost);
								player.sendMessage(ChatColor.BOLD + "You have bought " + l3 + " for " + cost + " points!");
								EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
								return;
							} else {
								player.sendMessage(ChatColor.RED + "You have " + n + " / " + ym.wepsignline3.get(l3) + " points to buy this.");
								return;
							}
							/* AREAS */
						} else if (l2.equalsIgnoreCase(ld.areastring)) {
							int cost = 1500;
							try {
								cost = Integer.parseInt(l3);
							} catch (Exception e) {
								e.printStackTrace();
							}
							sign.setLine(3, " " + cost + " ");
							if (zap.getPoints() >= cost) {
								GameArea a = getClosestArea(sign.getBlock(), (ZAGameBase) zap.getGame());
								if (a != null) {
									if (!a.isOpened()) {
										a.open();
										EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
										zap.subtractPoints(cost);
										player.sendMessage(ChatColor.BOLD + "You have bought an area for " + cost + " points.");
										return;
									} else
										player.sendMessage(ChatColor.RED + "This area has already been purchased!");
								} else
									player.sendMessage(ChatColor.RED + "There is no area close to this sign!");
								return;
							} else {
								player.sendMessage(ChatColor.RED + "You have " + zap.getPoints() + " / " + cost + " points to buy this.");
								return;
							}
						} else
							return;
					} else {
						player.sendMessage(ChatColor.RED + "You are level " + player.getLevel() + " / " + ym.levelmap.get(l3) + " required to buy that.");
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
	private void setupPlayerWithGame(String name, Player player) {
		ZAGame zag = data.findGame(l3);
		if (zag.getMainframe() == null)
			zag.setMainframe(player.getLocation());
		ZAPlayer zap = data.findZAPlayer(player, l3);
		GameCreateEvent gce = new GameCreateEvent(zag, null, player);
		Bukkit.getServer().getPluginManager().callEvent(gce);
		if (!gce.isCancelled())
			zap.loadPlayerToGame(l3);
		else
			zag.remove();
	}
}
