package com.github.JamesNorris.Manager;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import com.github.Ablockalypse;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Event.GameMobSpawnEvent;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameHellHound;
import com.github.JamesNorris.Implementation.GameUndead;
import com.github.JamesNorris.Interface.Barrier;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZALocation;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Util.Enumerated.GameEntityType;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;
import com.github.JamesNorris.Util.MathAssist;

public class SpawnManager {
	private ZAGame game;
	private Random rand;
	private World world;
	public ArrayList<ZAMob> mobs = new ArrayList<ZAMob>();

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
		rand = new Random();
	}

	/**
	 * @deprecated Causes mobs to spawn underground
	 *             Finds the closest air pocket to the checked location.
	 * 
	 * @param check The location to check for
	 * @return The new location with an open air pocket
	 */
	@Deprecated public Location findAirPocket(Location check) {// TODO make this work
		// int down = 0;
		// int up = 0;
		// if (check.getBlock().isEmpty()) {
		// for (int i = 0; i < 32; i++) {
		// Block b = check.subtract(0, i, 0).getBlock();
		// if (b.isEmpty()) {
		// ++down;
		// } else {
		// break;
		// }
		// }
		// if (down > 2) {
		// return check.subtract(0, down, 0).add(0, 2, 0);
		// }
		// } else {
		// for (int j = 0; j < 32; j++) {
		// Block b = check.add(0, j, 0).getBlock();
		// if (!b.isEmpty()) {
		// ++up;
		// } else {
		// break;
		// }
		// }
		// if (up > 1) {
		// return check.add(0, up, 0).add(0, 2, 0);
		// }
		// }
		return check;
	}

	/**
	 * Finds a good location to spawn a mob, relative to the location given.
	 * 
	 * @param l The location to relate to
	 * @param max The maximum distance in blocks from the location
	 * @param min The minimum distance in blocks from the location
	 */
	public Location findSpawnLocation(Location l, int max, int min) {
		int chance = rand.nextInt(4);
		World w = l.getWorld();
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		int modX = rand.nextInt(max - min) + min;
		int modZ = rand.nextInt(max - min) + min;
		float pitch = l.getPitch();
		float yaw = l.getYaw();
		if (chance == 1) {
			x = x - modX;
			z = z - modZ;
		} else if (chance == 2)
			x = x - modX;
		else if (chance == 3)
			z = z - modZ;
		Location newloc = new Location(w, x, y, z, yaw, pitch);
		return findAirPocket(newloc);
	}

	/**
	 * Spawns the specified entity, and makes it into a ZAMob.
	 * 
	 * @param l The location to spawn from
	 * @param et The EntityType to spawn
	 */
	public void gameSpawn(Location l, EntityType et) {
		Location loc = findAirPocket(l);
		Entity e = world.spawnEntity(loc, et);
		if (et == EntityType.ZOMBIE) {
			GameMobSpawnEvent gmse = new GameMobSpawnEvent(e, game, GameEntityType.UNDEAD);
			Bukkit.getServer().getPluginManager().callEvent(gmse);
			if (!gmse.isCancelled()) {
				GameUndead gu = new GameUndead((Zombie) e, game);
				mobs.add(gu);
			} else
				e.remove();
		} else if (et == EntityType.WOLF) {
			GameMobSpawnEvent gmse = new GameMobSpawnEvent(e, game, GameEntityType.HELLHOUND);
			Bukkit.getServer().getPluginManager().callEvent(gmse);
			if (!gmse.isCancelled()) {
				GameHellHound ghh = new GameHellHound((Wolf) e, game);
				mobs.add(ghh);
			} else
				e.remove();
		}
	}

	/**
	 * Gets the barrier closest to the location.
	 * 
	 * @param loc The location to check for
	 * @return The closest barrier
	 */
	public Barrier getClosestBarrier(Location loc) {
		int distance = Integer.MAX_VALUE;
		Barrier lp = null;// low priority
		Barrier hp = null;// high priority
		for (GameBarrier gb : game.getBarriers()) {
			Location l = gb.getCenter();
			int current = (int) MathAssist.distance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
			if (current < distance) {
				distance = current;
				lp = gb;
				if (pathIsClear(loc, l, current))
					hp = gb;
			}
		}
		if (hp != null)
			return hp;
		else if (lp != null)
			return lp;
		return null;
	}

	/**
	 * Gets the barrier closest to the player.
	 * 
	 * @param p The player to check for
	 * @return The closest barrier
	 */
	public Barrier getClosestBarrier(Player p) {
		return getClosestBarrier(p.getLocation());
	}

	/**
	 * Gets the closest spawner to the location.
	 * 
	 * @param loc The location to check for
	 * @return The closest spawner
	 */
	public ZALocation getClosestSpawner(Location loc) {
		int distance = Integer.MAX_VALUE;
		ZALocation lp = null;// low priority
		ZALocation hp = null;// high priority
		for (ZALocation l1 : game.getMobSpawners()) {
			Location l = l1.getBukkitLocation();
			int current = (int) MathAssist.distance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
			if (current < distance) {
				distance = current;
				lp = l1;
				if (pathIsClear(loc, l, current))
					hp = l1;
			}
		}
		if (hp != null)
			return hp;
		else if (lp != null)
			return lp;
		return null;
	}

	/**
	 * Gets the closest spawner to the player.
	 * 
	 * @param p The player to check for
	 * @return The closest spawner
	 */
	public ZALocation getClosestSpawner(Player p) {
		return getClosestSpawner(p.getLocation());
	}

	/**
	 * Gets the amount of spawns in the current level of the game.
	 * 
	 * @return The amount of spawns in the current level
	 */
	public int getCurrentSpawnAmount() {
		double m = 1.2;
		double x = game.getLevel();
		double b = game.getPlayers().size();
		int amt = (int) Math.round(MathAssist.line(m, x, b));
		if (game.isWolfRound())
			amt = amt / 3;
		return amt;
	}

	/**
	 * Gets all living mobs that have been spawned through this manager.
	 * 
	 * @return All mobs spawned by this manager, in an arraylist
	 */
	public ArrayList<ZAMob> getLivingMobs() {
		return mobs;
	}

	/**
	 * Gets the amount of spawns in the given level, with the given amount of players.
	 * 
	 * @param level The level you are looking for
	 * @param playeramt The amount of players you are looking for
	 * @param wolfround Whether or not the game is a wolf round or not
	 * @return The amount of spawns in this hypathetical level
	 */
	public int getSpawnAmount(int level, int playeramt, boolean wolfround) {
		double m = 1.2;
		double x = level;
		double b = playeramt;
		int amt = (int) Math.round(MathAssist.line(m, x, b));
		if (wolfround)
			amt = amt / 3;
		return amt;
	}

	/**
	 * Gets the world the SpawnManager uses to spawn mobs.
	 * 
	 * @return The world the SpawnManager manages
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * @deprecated Only checks for a direct path
	 *             Checks that the blocks from the one location to the next in the distance are empty.
	 * 
	 * @param start The starting location
	 * @param end The ending location
	 * @param distance The distance from the start to end
	 * @return Whether or not all blocks from start to end are empty
	 */
	@Deprecated public boolean pathIsClear(Location start, Location end, int distance) {// TODO get this working
		// ArrayList<Block> blocks = new ArrayList<Block>();
		// World w = start.getWorld();
		// for (int i2 = 1; i2 <= distance; i2++) {
		// int pX = start.getBlockX();
		// int pY = start.getBlockY();
		// int pZ = start.getBlockZ();
		// int eX = end.getBlockX();
		// int eY = end.getBlockY();
		// int eZ = end.getBlockZ();
		// int movX = eX - i2;
		// int movY = eY;
		// int movZ = eZ - i2;
		// if ((eX - pX) < 0)
		// movX = eX + i2;
		// if ((eZ - pZ) < 0)
		// movZ = eZ + i2;
		// Block block = w.getBlockAt(movX, movY, movZ);
		// Block block2 = block.getLocation().add(0, 1, 0).getBlock();
		// Block block3 = block.getLocation().subtract(0, 1, 0).getBlock();
		// if ((!block.isEmpty() && block2.isEmpty() && pY > eY))
		// block = block2;
		// if ((!block.isEmpty() && block3.isEmpty() && pY < eY))
		// block = block3;
		// blocks.add(block);
		// }
		// int size = blocks.size();
		// int known = 0;
		// for (Block b2 : blocks)
		// if (b2.isEmpty())
		// ++known;
		// return known == size;
		return false;
	}

	/**
	 * Gamespawns a mob at the specified location.
	 * 
	 * @param l The location to spawn the mob at
	 * @param closespawn Whether or not to spawn right next to the target
	 */
	public void spawn(Location l, boolean closespawn) {
		if (game.isWolfRound()) {
			if (!closespawn)
				l = findSpawnLocation(l, 7, 4);
			gameSpawn(l, EntityType.WOLF);
		} else {
			if (!closespawn)
				l = findSpawnLocation(l, 16, 10);
			gameSpawn(l, EntityType.ZOMBIE);
		}
	}

	/**
	 * Spawns a wave of mobs around random living players in this game.
	 * If barriers are present and accessible, spawns the mobs at the barriers.
	 * If mob spawners are set in the game and are accessible, spawns the mobs at a mob spawner.
	 * This will only spawn mobs if the game has a mob count of 0.
	 */
	public void spawnWave() {
		int amt = getCurrentSpawnAmount();
		if (External.getYamlManager().getConfigurationData().DEBUG)
			System.out.println("[Ablockalypse] [DEBUG] Amount of zombies in this wave: (" + game.getName() + ") " + amt);
		if (game.getRemainingPlayers() >= 1 && game.getMobCount() <= 0)
			for (int i = 0; i <= amt; i++)
				Bukkit.getScheduler().scheduleSyncDelayedTask(Ablockalypse.instance, new Runnable() {
					@Override public void run() {
						if (game.getRemainingPlayers() >= 1 && !game.isPaused()) {
							Player p = game.getRandomLivingPlayer();
							if (game.getMobSpawners().size() > 0) {
								ZALocation zaloc = getClosestSpawner(p);
								game.spawn(zaloc.getBukkitLocation().add(0, 2, 0), true);
								zaloc.playEffect(ZAEffect.FLAMES);
								zaloc.getBukkitLocation().subtract(0, 2, 0);
							} else if (game.getBarriers().size() > 0)
								game.spawn(getClosestBarrier(p).getSpawnLocation(), true);
							else
								game.spawn(p.getLocation(), false);
						}
					}
				}, (i * 40));
		else
			game.end();
	}
}
