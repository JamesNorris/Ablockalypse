package com.github.JamesNorris.Implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.Ablockalypse;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Event.GamePlayerJoinEvent;
import com.github.JamesNorris.Event.LastStandEvent;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Threading.LastStandThread;
import com.github.JamesNorris.Util.Breakable;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.Enumerated.PlayerStatus;
import com.github.JamesNorris.Util.Enumerated.PowerupType;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;
import com.github.JamesNorris.Util.Enumerated.ZAPerk;
import com.github.JamesNorris.Util.Enumerated.ZASound;
import com.github.JamesNorris.Util.MiscUtil;
import com.github.JamesNorris.Util.SoundUtil;

public class ZAPlayerBase implements ZAPlayer, GameObject {
	private Location before;
	private ConfigurationData cd;
	private float exp, saturation, fall, exhaust;
	private ZAGame game;
	private GameMode gm;
	private ItemStack[] inventory, armor;
	private boolean laststand, sleepingignored, sent, limbo, teleporting, instakill;
	private int level, health, food, fire, points;
	private String name;
	private Player player;
	private HashMap<String, Integer> point;
	private Collection<PotionEffect> pot;
	private ArrayList<ZAPerk> perks = new ArrayList<ZAPerk>();

	/**
	 * Creates a new instance of a ZAPlayer, using an instance of a Player.
	 * 
	 * NOTE: This instance comes with a built-in ZASoundManager.
	 * 
	 * @param player The player to be made into this instance
	 * @param game The game this player should be in
	 */
	public ZAPlayerBase(Player player, ZAGame game) {
		GlobalData.objects.add(this);
		cd = External.ym.getConfigurationData();
		this.player = player;
		name = player.getName();
		this.game = game;
		point = new HashMap<String, Integer>();
		GlobalData.players.put(player, this);
		player.setLevel(game.getLevel());
	}

	/**
	 * Adds a perk and effect to the player.
	 * 
	 * @param perk The type of perk to add to the player
	 * @param duration The duration of the perk
	 * @param power The power of the perk
	 */
	@Override public void addPerk(ZAPerk perk, int duration, int power) {
		perks.add(perk);
		switch (perk) {
			case DAMAGE:
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, power));
			break;
			case HEAL:
				player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, duration, power));
			break;
			case REGENERATE:
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, power));
			break;
			case SPEED:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, power));
			break;
		}
	}

	/**
	 * Gives points to the player.
	 * 
	 * @param i The amount of points to give the player
	 */
	@Override public void addPoints(int i) {
		points = points + i;
		if (point.containsKey(getName()))
			point.remove(getName());
		point.put(getName(), points);
		if (GlobalData.playerPoints.containsKey(game.getName()))
			GlobalData.playerPoints.remove(game.getName());
		GlobalData.playerPoints.put(game.getName(), point);
		rename(name, "" + points);
	}

	/**
	 * Gets the game the player is currently in
	 * 
	 * @return The game the player is in
	 */
	@Override public ZAGame getGame() {
		return game;
	}

	/**
	 * Returns the players' name.
	 * 
	 * @return The name of the player
	 */
	@Override public String getName() {
		return name;
	}

	/**
	 * Gets a list of perks that the player has attached to them.
	 * 
	 * @return A list of perks used by the player
	 */
	@Override public ArrayList<ZAPerk> getPerks() {
		return perks;
	}

	/**
	 * Gets the Player instance of this ZAPlayer.
	 * 
	 * @return The player instance involved with this instance
	 */
	@Override public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the points the player currently has.
	 * 
	 * @return The amount of points the player has
	 */
	@Override public int getPoints() {
		return points;
	}

	/**
	 * Gets the status of the player.
	 * 
	 * @return The current status of the player
	 */
	@Override public PlayerStatus getStatus() {
		if (limbo)
			return PlayerStatus.LIMBO;
		if (laststand)
			return PlayerStatus.LAST_STAND;
		if (teleporting)
			return PlayerStatus.TELEPORTING;
		return null;
	}

	/**
	 * Gives the player the specified powerup.
	 * 
	 * @param type The type of powerup to give the player
	 * @param cause The entity that originated this event
	 */
	@Override public void givePowerup(PowerupType type, Entity cause) {
		switch (type) {
			case ATOM_BOMB:
				game.broadcast(ChatColor.GRAY + "ATOM BOMB!", null);
				for (ZAMob zam : GlobalData.getZAMobs())
					if (zam.getGame() == game) {
						SoundUtil.generateSound(zam.getWorld(), zam.getEntity().getLocation(), ZASound.EXPLOSION);
						EffectUtil.generateEffect(player, zam.getEntity().getLocation(), ZAEffect.FLAMES);
						zam.kill();
					}
				for (String s2 : game.getPlayers()) {
					Player p = Bukkit.getPlayer(s2);
					ZAPlayer zap = GlobalData.findZAPlayer(p, game.getName());
					zap.addPoints(cd.atompoints);
				}
			break;
			case BARRIER_FIX:
				game.broadcast(ChatColor.GRAY + "BARRIERS FIXED!", null);
				if (GlobalData.gamebarriers.size() >= 1)
					for (GameBarrier b : game.getBarriers())
						b.replacePanels();
			break;
			case WEAPON_FIX:
				game.broadcast(ChatColor.GRAY + "WEAPONS FIXED!", null);
				for (String s3 : game.getPlayers()) {
					Player p = Bukkit.getPlayer(s3);
					Inventory i = p.getInventory();
					for (ItemStack it : i.getContents())
						if (MiscUtil.isSword(it)) {
							it.setDurability((short) 0);
							EffectUtil.generateEffect(p, ZAEffect.EXTINGUISH);
						}
				}
			break;
			case INSTA_KILL:
				game.broadcast(ChatColor.GRAY + "INSTA_KILL!", null);
				for (String s3 : game.getPlayers()) {
					Player p = Bukkit.getPlayer(s3);
					if (GlobalData.playerExists(p)) {
						final ZAPlayer zap = GlobalData.getZAPlayer(p);
						zap.setInstaKill(true);
						Bukkit.getScheduler().scheduleSyncDelayedTask(Ablockalypse.instance, new Runnable() {
							@Override public void run() {
								zap.setInstaKill(false);
							}
						}, 300);
					}
				}
		}
	}

	/**
	 * Checks if the player has insta-kill enabled.
	 * 
	 * @return Whether or not the player has insta-kill
	 */
	@Override public boolean hasInstaKill() {
		return instakill;
	}

	/**
	 * Returns true if the player is in last stand
	 * 
	 * @return Whether or not the player is in last stand
	 */
	@Override public boolean isInLastStand() {
		return laststand;
	}

	/**
	 * Gets whether or not the player is in limbo.
	 * 
	 * @return Whether or not the player is in limbo
	 */
	@Override public boolean isInLimbo() {
		return limbo;
	}

	/**
	 * Checks if the player is teleporting or not.
	 * 
	 * @return Whether or not the player is teleporting
	 */
	@Override public boolean isTeleporting() {
		return teleporting;
	}

	/**
	 * Checks if the name given is the name of a game. If not, creates a new game.
	 * Then, adds the player to that game with all settings completed.
	 * 
	 * @param name The name of the player to be loaded into the game
	 */
	@Override public void loadPlayerToGame(String name) {
		/* Use an old game to add the player to the game */
		if (GlobalData.games.containsKey(name)) {
			ZAGameBase zag = GlobalData.games.get(name);
			GamePlayerJoinEvent GPJE = new GamePlayerJoinEvent(this, zag);
			Bukkit.getPluginManager().callEvent(GPJE);
			if (!GPJE.isCancelled()) {
				int max = cd.maxplayers;
				if (zag.getPlayers().size() < max) {
					zag.addPlayer(player);
					saveStatus();
					prepForGame();
					if (game.getMainframe() == null)
						game.setMainframe(player.getLocation());
					sendToMainframe("Loading player to a game");
					player.sendMessage(ChatColor.GRAY + "You have joined the game: " + name);
					return;
				} else
					player.sendMessage(ChatColor.RED + "This game has " + max + "/" + max + " players!");
			}
		}
	}

	/*
	 * Clearing the player status to allow the player to be put in the game without carrying over items.
	 */
	@SuppressWarnings("deprecation") private void prepForGame() {
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.setLevel(0);
		player.setExp(0);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(0);
		player.getActivePotionEffects().clear();
		player.getInventory().setArmorContents(null);
		player.setSleepingIgnored(true);
		player.setFireTicks(0);
		player.setFallDistance(0F);
		player.setExhaustion(0F);
		rename(name, "0");
		// try {//TODO fix this
		// for (String s : cd.inventory) {
		// player.getInventory().addItem(StartingItems.seperateStartingItemsData(s));
		// }
		// player.getInventory().setHelmet(StartingItems.seperateStartingItemsData(cd.helmet));
		// player.getInventory().setChestplate(StartingItems.seperateStartingItemsData(cd.chestplate));
		// player.getInventory().setLeggings(StartingItems.seperateStartingItemsData(cd.leggings));
		// player.getInventory().setBoots(StartingItems.seperateStartingItemsData(cd.boots));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		player.updateInventory();
	}

	/**
	 * Removes the player completely.
	 */
	@Override public void remove() {
		removeFromGame();
	}

	/**
	 * Removes the player from the game, and removes all data from the player.
	 */
	@Override public void removeFromGame() {
		restoreStatus();
		if (game.getPlayers().contains(player.getName()))
			game.removePlayer(player);
		GlobalData.objects.remove(this);
	}

	/*
	 * Checks that the name and suffix are lower than 16 chars.
	 * Any higher and the name is truncated.
	 */
	private void rename(String name, String suffix) {
		String mod = name;
		int cutoff = 16 - (suffix.length() + 1);
		if (name.length() > cutoff)
			mod = name.substring(0, cutoff);
		player.setDisplayName(mod + " " + suffix);
	}

	/*
	 * Restoring the player status to the last saved status before the game.
	 */
	@SuppressWarnings("deprecation") private void restoreStatus() {
		if (laststand)
			toggleLastStand();
		if (gm != null) {
			for (PotionEffect pe : player.getActivePotionEffects()) {
				PotionEffectType pet = pe.getType();
				player.removePotionEffect(pet);
			}
			player.setGameMode(gm);
			player.teleport(before);
			player.getInventory().clear();
			player.getInventory().setContents(inventory);
			player.setLevel(level);
			player.setExp(exp);
			player.setHealth(health);
			player.setFoodLevel(food);
			player.setSaturation(saturation);
			player.addPotionEffects(pot);
			player.getInventory().setArmorContents(armor);
			player.setSleepingIgnored(sleepingignored);
			player.setFireTicks(fire);
			player.setFallDistance(fall);
			player.setExhaustion(exhaust);
			player.setDisplayName(name);
			player.updateInventory();
		}
	}

	/*
	 * Saving the player status, so when the player is removed from the game, they are set back to where they were before.
	 */
	private void saveStatus() {
		before = player.getLocation();
		inventory = player.getInventory().getContents();
		exp = player.getExp();
		level = player.getLevel();
		health = player.getHealth();
		food = player.getFoodLevel();
		saturation = player.getSaturation();
		pot = player.getActivePotionEffects();
		armor = player.getInventory().getArmorContents();
		sleepingignored = player.isSleepingIgnored();
		fire = player.getFireTicks();
		fall = player.getFallDistance();
		exhaust = player.getExhaustion();
		gm = player.getGameMode();
		SoundUtil.generateSound(player, ZASound.START);
	}

	/**
	 * Teleports the player to the mainframe of the game.
	 * 
	 * @param reason The reason for teleportation for the debug mode
	 */
	@Override public void sendToMainframe(String reason) {
		player.sendMessage(ChatColor.GRAY + "Teleporting to mainframe...");
		Location loc = game.getMainframe();
		Chunk c = loc.getChunk();
		if (!c.isLoaded())
			c.load();
		player.teleport(loc);
		if (sent) {
			SoundUtil.generateSound(loc.getWorld(), loc, ZASound.START);
			sent = true;
		} else
			SoundUtil.generateSound(loc.getWorld(), loc, ZASound.TELEPORT);
		if (cd.DEBUG)
			System.out.println("[Ablockalypse] [DEBUG] Mainframe TP reason: (" + game.getName() + ") " + reason);
	}

	/**
	 * Enables insta-kill for this player.
	 * 
	 * @param tf Whether or not to start/cancel insta-kill
	 */
	@Override public void setInstaKill(boolean tf) {
		instakill = tf;
	}

	/**
	 * Changes the player limbo status.
	 */
	@Override public void setLimbo(boolean tf) {
		limbo = tf;
	}

	/**
	 * Changes the teleportation status of the player.
	 * 
	 * @param tf What to change the status to
	 */
	@Override public void setTeleporting(boolean tf) {
		teleporting = tf;
	}

	/**
	 * Removes points from the player.
	 * 
	 * @param i The amount of points to remove from the player
	 */
	@Override public void subtractPoints(int i) {
		points = points - i;
	}

	/**
	 * Teleport the player to the specified location, with the specified reason for the debug mode.
	 * 
	 * @param location The location to teleport to
	 * @param reason The reason for teleportation
	 */
	@Override public void teleport(Location location, String reason) {
		player.teleport(location);
		if (cd.DEBUG)
			System.out.println("[Ablockalypse] [DEBUG] TP reason: (" + game.getName() + ") " + reason);
	}

	/**
	 * Teleports the player to the specified location,
	 * with the specified arguments, and the specified reason for the debug mode.
	 * 
	 * @param world The world to teleport in
	 * @param x The x coord to teleport to
	 * @param y The y coord to teleport to
	 * @param z The z coord to teleport to
	 * @param reason The reason for teleportation
	 */
	@Override public void teleport(World world, int x, int y, int z, String reason) {
		player.teleport(world.getBlockAt(x, y, z).getLocation());
		if (cd.DEBUG)
			System.out.println("[Ablockalypse] [DEBUG] TP reason: (" + game.getName() + ") " + reason);
	}

	/**
	 * Toggles sitting for the player.
	 */
	@Override public void toggleLastStand() {
		if (!laststand) {
			LastStandEvent lse = new LastStandEvent(player, this, true);
			Bukkit.getServer().getPluginManager().callEvent(lse);
			if (!lse.isCancelled())
				if (!(getGame().getRemainingPlayers() <= 1)) {
					player.sendMessage(ChatColor.GRAY + "You have been knocked down!");
					laststand = true;
					Entity v = player.getVehicle();
					if (v != null)
						v.remove();
					rename(name, "[LS]");
					player.setFoodLevel(0);
					player.setHealth(5);
					SoundUtil.generateSound(player, ZASound.LAST_STAND);
					Breakable.setSitting(player, true);
					game.broadcast(ChatColor.RED + name + ChatColor.GRAY + " is down and needs revival", player);
					new LastStandThread(this, true);
					if (cd.losePerksLastStand)
						player.getActivePotionEffects().clear();
					player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 1));// TODO test
				} else
					removeFromGame();
		} else {
			LastStandEvent lse = new LastStandEvent(player, this, false);
			Bukkit.getServer().getPluginManager().callEvent(lse);
			if (!lse.isCancelled()) {
				for (PotionEffect pe : player.getActivePotionEffects())
					if (pe.getType() == PotionEffectType.CONFUSION)
						player.removePotionEffect(pe.getType());
				player.sendMessage(ChatColor.GRAY + "You have been picked up!");
				game.broadcast(ChatColor.RED + name + ChatColor.GRAY + " has been revived.", player);
				laststand = false;
				Breakable.setSitting(player, false);
				if (player.getVehicle() != null)
					player.getVehicle().remove();
				player.setFoodLevel(20);
				Entity v = player.getVehicle();
				if (v != null)
					v.remove();
			}
		}
	}
}
