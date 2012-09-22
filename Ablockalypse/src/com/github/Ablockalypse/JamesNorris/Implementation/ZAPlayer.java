package com.github.Ablockalypse.JamesNorris.Implementation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet40EntityMetadata;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Data.ByteData;
import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Interface.ZAPlayerInterface;
import com.github.Ablockalypse.JamesNorris.Manager.SoundManager;
import com.github.Ablockalypse.JamesNorris.Manager.SoundManager.ZASound;
import com.github.Ablockalypse.JamesNorris.Util.ControlledEffect;
import com.github.Ablockalypse.JamesNorris.Util.External;
import com.github.Ablockalypse.JamesNorris.Util.Square;
import com.github.Ablockalypse.JamesNorris.Util.Util;
import com.github.Ablockalypse.JamesNorris.Util.Util.PowerupType;
import com.github.Ablockalypse.iKeirNez.Util.StartingItems;

public class ZAPlayer implements ZAPlayerInterface {
	private final ConfigurationData cd;
	private float exp, saturation, fall, exhaust;
	private final ZAGame game;
	private GameMode gm;
	private ItemStack[] inventory, armor;
	private boolean laststand, sleepingignored, broken;
	private int level, health, food, fire, points;
	private final String name;
	private final Player player;
	private HashMap<String, Integer> point;
	private Collection<PotionEffect> pot;
	private final SoundManager sound;
	private Location spawn;

	/**
	 * Creates a new instance of a ZAPlayer, using an instance of a Player.
	 * 
	 * NOTE: This instance comes with a built-in ZASoundManager.
	 * 
	 * @param player The player to be made into this instance
	 * @param game The game this player should be in
	 */
	public ZAPlayer(final Player player, final ZAGame game) {
		cd = External.ym.getConfigurationData();
		this.player = player;
		name = player.getName();
		this.game = game;
		sound = new SoundManager(player);
		Data.players.put(player, this);
	}

	/**
	 * Gives points to the player.
	 * 
	 * @param i The amount of points to give the player
	 */
	@Override public void addPoints(final int i) {
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

	/*
	 * Creates a new Packet40EntityMetadata packet for players standing up or sitting down.
	 * 
	 * NOTE: The 2 options for the bit byte are 0x00 (down) and 0x04 (up).
	 */
	private void generatePacket(final Player player, final byte bit) {
		try {
			if (!broken) {
				player.teleport(player.getLocation().add(0, 1, 0));
				final Packet packet = new Packet40EntityMetadata(player.getEntityId(), new ByteData(bit));
				for (final Player p : Bukkit.getServer().getOnlinePlayers())
					((CraftPlayer) p).getHandle().netServerHandler.sendPacket(packet);
			}
		} catch (final Exception e) {
			broken = true;
			Ablockalypse.crash(e.getCause().toString(), false);
		}
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
	@Override public void givePowerup(final PowerupType type) {
		final Location loc = player.getLocation();
		final int radius = cd.powerrad;
		switch (type) {
			case ATOM_BOMB:
				final Square s = new Square(loc, radius);
				final List<Location> locs = s.getLocations();
				for (final GameZombie gz : Data.zombies) {
					final Zombie z = gz.getZombie();
					if (locs.contains(z.getLocation())) {
						z.remove();
						for (final String s2 : game.getPlayers()) {
							final Player p = Bukkit.getPlayer(s2);
							final ZAPlayer zap = Data.findZAPlayer(p, game.getName());
							zap.addPoints(cd.atompoints);
						}
					}
				}
				if (cd.effects)
					new ControlledEffect(player.getWorld(), Effect.MOBSPAWNER_FLAMES, radius, 1, loc, true);
				break;
			case BARRIER_FIX:
				final Square s2 = new Square(loc, radius);
				final List<Location> locs2 = s2.getLocations();
				for (final Barrier b : Data.gamebarriers) {
					if (locs2.contains(b.getCenter()))
						b.replaceBarrier();
				}
				break;
			case WEAPON_FIX:
				for (final String s3 : game.getPlayers()) {
					final Player p = Bukkit.getPlayer(s3);
					final Inventory i = p.getInventory();
					for (final ItemStack it : i.getContents()) {
						if (Util.isWeapon(it)) {
							it.setDurability((short) 0);
							p.getWorld().playEffect(p.getLocation(), Effect.EXTINGUISH, 1);
						}
					}
				}
				break;
		}
	}

	/**
	 * Gets the SoundManager instance associated with this instance.
	 * 
	 * @return The SoundManager associated with this instance
	 */
	@Override public SoundManager getSoundManager() {
		return sound;
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
	 * Checks if the name given is the name of a game. If not, creates a new game.
	 * Then, adds the player to that game with all settings completed.
	 * 
	 * @param name The name of the player to be loaded into the game
	 */
	@Override public void loadPlayerToGame(final String name) {
		/* Use an old game to add the player to the game */
		if (Data.games.containsKey(name)) {
			final ZAGame zag = Data.games.get(name);
			final int max = cd.maxplayers;
			if (zag.getPlayers().size() < max) {
				zag.addPlayer(player);
				saveStatus();
				prepForGame();
				sendToMainframe();
				return;
			} else {
				player.sendMessage(ChatColor.RED + "This game has " + max + "/" + max + " players!");
			}
			/* Create a new game, and put the player in that game */
		} else {
			final ZAGame zag = new ZAGame(name, cd, true);
			zag.setSpawn(Data.mainframes.get(name));
			zag.addPlayer(player);
			saveStatus();
			prepForGame();
			sendToMainframe();
			return;
		}
	}

	/*
	 * Clearing the player status to allow the player to be put in the game without carrying over items.
	 */
	private void prepForGame() {
		player.getInventory().clear();
		player.setLevel(0);
		player.setExp(0);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(0);
		player.getActivePotionEffects().clear();
		player.getInventory().setArmorContents(null);
		player.setSleepingIgnored(true);
		player.setBedSpawnLocation(game.getSpawn());
		player.setFireTicks(0);
		player.setFallDistance(0F);
		player.setExhaustion(0F);
		player.setGameMode(GameMode.SURVIVAL);
		try {
			for (final String s : cd.inventory) {
				player.getInventory().addItem(StartingItems.seperateStartingItemsData(s));
			}
			player.getInventory().setHelmet(StartingItems.seperateStartingItemsData(cd.helmet));
			player.getInventory().setChestplate(StartingItems.seperateStartingItemsData(cd.chestplate));
			player.getInventory().setLeggings(StartingItems.seperateStartingItemsData(cd.leggings));
			player.getInventory().setBoots(StartingItems.seperateStartingItemsData(cd.boots));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Restoring the player status to the last saved status before the game.
	 */
	private void restoreStatus() {
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
		player.setBedSpawnLocation(spawn);
		player.setFireTicks(fire);
		player.setFallDistance(fall);
		player.setExhaustion(exhaust);
		player.setGameMode(gm);
		if (laststand)
			toggleLastStand();
	}

	/*
	 * Saving the player status, so when the player is removed from the game, they are set back to where they were before.
	 */
	private void saveStatus() {
		inventory = player.getInventory().getContents();
		exp = player.getExp();
		level = player.getLevel();
		health = player.getHealth();
		food = player.getFoodLevel();
		saturation = player.getSaturation();
		pot = player.getActivePotionEffects();
		armor = player.getInventory().getArmorContents();
		sleepingignored = player.isSleepingIgnored();
		spawn = player.getBedSpawnLocation();
		fire = player.getFireTicks();
		fall = player.getFallDistance();
		exhaust = player.getExhaustion();
		gm = player.getGameMode();
		sound.generateSound(ZASound.START);
	}

	/**
	 * Teleports the player to the mainframe of the game.
	 */
	@Override public void sendToMainframe() {
		player.teleport(game.getSpawn());
		sound.generateSound(ZASound.TELEPORT);
	}

	/**
	 * Removes points from the player.
	 * 
	 * @param i The amount of points to remove from the player
	 */
	@Override public void subtractPoints(final int i) {
		points = points - i;
	}

	/**
	 * Toggles sitting for the player.
	 */
	@Override public void toggleLastStand() {
		if (!laststand) {
			laststand = true;
			final Entity v = player.getVehicle();
			if (v != null && !player.isSneaking())
				v.remove();
			player.setAllowFlight(true);
			player.setFlying(true);
			generatePacket(player, (byte) 0x00);
			sound.generateSound(ZASound.LAST_STAND);
			if (cd.losePerksLastStand)
				player.getActivePotionEffects().clear();
		} else {
			laststand = false;
			player.setAllowFlight(false);
			player.setFlying(false);
			generatePacket(player, (byte) 0x04);
		}
	}
}
