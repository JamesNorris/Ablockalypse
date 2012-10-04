package com.github.JamesNorris.Implementation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Event.LastStandEvent;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Threading.LastStandThread;
import com.github.JamesNorris.Util.Breakable;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.SoundUtil;
import com.github.JamesNorris.Util.Square;
import com.github.JamesNorris.Util.MiscUtil;
import com.github.JamesNorris.Util.EffectUtil.ZAEffect;
import com.github.JamesNorris.Util.MiscUtil.PowerupType;
import com.github.JamesNorris.Util.SoundUtil.ZASound;

public class ZAPlayerBase implements ZAPlayer {
	private Location before;
	private ConfigurationData cd;
	private float exp, saturation, fall, exhaust;
	private ZAGame game;
	private GameMode gm;
	private ItemStack[] inventory, armor;
	private boolean laststand, sleepingignored, sent, limbo;
	private int level, health, food, fire, points;
	private String name;
	private Player player;
	private HashMap<String, Integer> point;
	private Collection<PotionEffect> pot;

	/**
	 * Creates a new instance of a ZAPlayer, using an instance of a Player.
	 * 
	 * NOTE: This instance comes with a built-in ZASoundManager.
	 * 
	 * @param player The player to be made into this instance
	 * @param game The game this player should be in
	 */
	public ZAPlayerBase(Player player, ZAGame game) {
		this.cd = External.ym.getConfigurationData();
		this.player = player;
		this.name = player.getName();
		this.game = game;
		this.point = new HashMap<String, Integer>();
		Data.players.put(player, this);
		player.setLevel(1);
		if (game.getLevel() == 0)
			game.nextLevel();
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
		if (Data.playerPoints.containsKey(game.getName()))
			Data.playerPoints.remove(game.getName());
		Data.playerPoints.put(game.getName(), point);
	}

	/**
	 * Removes all data associated with the player, as well as restoring the data of the player before they joined the game.
	 */
	@SuppressWarnings("unused") @Override public void finalize() {
		restoreStatus();
		for (Method m : this.getClass().getDeclaredMethods())
			m = null;
		for (Field f : this.getClass().getDeclaredFields())
			f = null;
	}

	/**
	 * Gets the game the player is currently in
	 * 
	 * @return The game the player is in
	 */
	@Override public ZAGame getGame() {
		return (ZAGame) game;
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
	 * Gives the player the specified powerup.
	 */
	@Override public void givePowerup(PowerupType type) {
		Location loc = player.getLocation();
		int radius = cd.powerrad;
		switch (type) {
			case ATOM_BOMB:
				for (GameUndead gz : Data.undead) {
					if (gz.getGame() == this.getGame()) {
						Zombie z = gz.getZombie();
						int prev = game.getRemainingMobs();
						EffectUtil.generateEffect(player, z.getLocation(), ZAEffect.FLAMES);
						z.remove();
						if (prev < game.getRemainingMobs())
							game.subtractMobCount();
					}
				}
				for (String s2 : game.getPlayers()) {
					Player p = Bukkit.getPlayer(s2);
					ZAPlayer zap = Data.findZAPlayer(p, game.getName());
					zap.addPoints(cd.atompoints);
				}
				EffectUtil.generateEffect(player, loc, ZAEffect.FLAMES);
			break;
			case BARRIER_FIX:
				if (Data.gamebarriers.size() >= 1) {
					Square s2 = new Square(loc, radius);
					List<Location> locs2 = s2.getLocations();
					for (GameBarrier b : Data.gamebarriers) {
						if (locs2.contains(b.getCenter()))
							b.replaceBarrier();
					}
				}
			break;
			case WEAPON_FIX:
				for (String s3 : game.getPlayers()) {
					Player p = Bukkit.getPlayer(s3);
					Inventory i = p.getInventory();
					for (ItemStack it : i.getContents()) {
						if (MiscUtil.isWeapon(it)) {
							it.setDurability((short) 0);
							EffectUtil.generateEffect(p, ZAEffect.EXTINGUISH);
						}
					}
				}
			break;
		}
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
	 * Checks if the name given is the name of a game. If not, creates a new game.
	 * Then, adds the player to that game with all settings completed.
	 * 
	 * @param name The name of the player to be loaded into the game
	 */
	@Override public void loadPlayerToGame(String name) {
		/* Use an old game to add the player to the game */
		if (Data.games.containsKey(name)) {
			ZAGameBase zag = Data.games.get(name);
			int max = cd.maxplayers;
			if (zag.getPlayers().size() < max) {
				zag.addPlayer(player);
				saveStatus();
				prepForGame();
				sendToMainframe("Loading player to a game");
				player.sendMessage(ChatColor.GRAY + "You have joined the game: " + name);
				return;
			} else {
				player.sendMessage(ChatColor.RED + "This game has " + max + "/" + max + " players!");
			}
			/* Create a new game, and put the player in that game */
		}/* else {//unused because this is already compensated for with the commands
			ZAGameBase zag = new ZAGameBase(name, cd);
			zag.setSpawn(Data.mainframes.get(name));
			zag.addPlayer(player);
			saveStatus();
			prepForGame();
			sendToMainframe("Loading NEW game");
			player.sendMessage(ChatColor.GRAY + "You have joined the game: " + name);
			return;
			}*/
	}

	/*
	 * Clearing the player status to allow the player to be put in the game without carrying over items.
	 */
	private void prepForGame() {
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
	}

	/**
	 * Removes the player from the game, and removes all data from the player.
	 */
	@Override public void removeFromGame() {
		finalize();
	}

	/*
	 * Restoring the player status to the last saved status before the game.
	 */
	private void restoreStatus() {
		if (laststand)
			toggleLastStand();
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
	 */
	@Override public void sendToMainframe(String reason) {
		player.sendMessage(ChatColor.GRAY + "Teleporting to mainframe...");
		Location loc = game.getSpawn();
		Chunk c = loc.getChunk();
		if (!c.isLoaded())
			c.load();
		player.teleport(loc);
		if (sent) {
			SoundUtil.generateSound(player, ZASound.START);
			sent = true;
		} else {
			SoundUtil.generateSound(player, ZASound.TELEPORT);
		}
		if (cd.DEBUG)
			System.out.println("[Ablockalypse] [DEBUG] Teleport reason: (" + game.getName() + ") " + reason);
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
	 * Toggles sitting for the player.
	 */
	@Override public void toggleLastStand() {
		if (!laststand) {
			LastStandEvent lse = new LastStandEvent(player, this, true);
			Bukkit.getServer().getPluginManager().callEvent(lse);
			if (!lse.isCancelled()) {
				laststand = true;
				Entity v = player.getVehicle();
				if (v != null)
					v.remove();
				player.setFoodLevel(5);
				SoundUtil.generateSound(player, ZASound.LAST_STAND);
				Breakable.setSitting(player, true);
				new LastStandThread((ZAPlayer) this, true);
				if (cd.losePerksLastStand)
					player.getActivePotionEffects().clear();
			}
		} else {
			LastStandEvent lse = new LastStandEvent(player, this, false);
			Bukkit.getServer().getPluginManager().callEvent(lse);
			if (!lse.isCancelled()) {
				laststand = false;
				Breakable.setSitting(player, false);
				if (player.getVehicle() != null)
					player.getVehicle().remove();
				player.setFoodLevel(5);
				Entity v = player.getVehicle();
				if (v != null)
					v.remove();
			}
		}
	}

	/**
	 * Toggles the player limbo status.
	 */
	@Override public void toggleLimbo() {
		if (limbo)
			limbo = false;
		else
			limbo = true;
	}
}
