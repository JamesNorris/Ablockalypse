package com.github.JamesNorris.Manager;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import com.github.JamesNorris.Event.GameMobSpawnEvent;
import com.github.JamesNorris.Implementation.GameHellHound;
import com.github.JamesNorris.Implementation.GameUndead;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Util.MiscUtil.GameEntityType;

public class SpawnManager {
	private ZAGame game;
	private Random rand;
	private World world;

	/**
	 * Creates a new spawn manager for spawning mobs in a game.
	 * This instance is used for getting a valid point to spawn mobs at,
	 * and also for spawning the mob with all the game changes necessary.
	 * 
	 * @param game The game to spawn the mobs in
	 */
	public SpawnManager(ZAGame game, World world) {
		this.game = game;
		this.world = world;
		this.rand = new Random();
	}

	/*
	 * Checks if the location given is open to spawn, and if not adds to the location y.
	 */
	private Location capLocation(Location check) {
		Location loc;
		if (!(check.getBlock().isEmpty() && check.add(0, 1, 0).getBlock().isEmpty()))
			loc = world.getHighestBlockAt(check).getLocation();
		else
			loc = check;
		return loc;
	}

	/**
	 * Finds a good location to spawn a mob, relative to the player.
	 * 
	 * @param l The location of the player
	 * @param max The maximum distance in blocks from the player
	 * @param min The minimum distance in blocks from the player
	 */
	public Location findSpawnLocation(Location l, int max, int min) {
		int chance = rand.nextInt(4);
		World w = l.getWorld();
		int x = rand.nextInt(max - min) + min;
		int z = rand.nextInt(max - min) + min;
		int y = l.getBlockY();
		float pitch = l.getPitch();
		float yaw = l.getYaw();
		if (chance == 1) {
			x = 0 - x;
			z = 0 - z;
		} else if (chance == 2)
			x = 0 - x;
		else if (chance == 3)
			z = 0 - z;
		Location newloc = new Location(w, x, y, z, yaw, pitch);
		return capLocation(newloc);
	}

	/**
	 * Spawns the specified entity, and should only be called after using findSpawnLocation().
	 * 
	 * @param l The location to spawn from
	 * @param et The EntityType to spawn
	 */
	public void gameSpawn(Location l, EntityType et) {// TODO come up with a way to improve the distance for targetting players
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		Player p = game.getRandomPlayer();
		Location ploc = p.getLocation();
		int x2 = ploc.getBlockX();
		int z2 = ploc.getBlockZ();
		Location newloc = world.getBlockAt((x2 + x), y, (z2 + z)).getLocation();
		Location loc = capLocation(newloc);
		Entity e = world.spawnEntity(loc, et);
		if (et == EntityType.ZOMBIE) {
			GameMobSpawnEvent gmse = new GameMobSpawnEvent(e, game, GameEntityType.UNDEAD);
			Bukkit.getServer().getPluginManager().callEvent(gmse);
			if (!gmse.isCancelled()) {
				new GameUndead((Zombie) e, game);
			}
		} else if (et == EntityType.WOLF) {
			GameMobSpawnEvent gmse = new GameMobSpawnEvent(e, game, GameEntityType.HELLHOUND);
			Bukkit.getServer().getPluginManager().callEvent(gmse);
			if (!gmse.isCancelled()) {
				new GameHellHound((Wolf) e, game);
			}
		}
	}
}
