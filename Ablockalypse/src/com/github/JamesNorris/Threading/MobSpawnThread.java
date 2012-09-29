package com.github.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.PathfinderGoalMoveTowardsTarget;

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
import com.github.JamesNorris.Implementation.GameHellHound;
import com.github.JamesNorris.Implementation.GameUndead;
import com.github.JamesNorris.Interface.HellHound;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Util.Breakable;
import com.github.JamesNorris.Util.MathAssist;

public class MobSpawnThread {
	private long curvefitVariable;
	private ZAGame game;
	private int i, id, level, players;
	private Ablockalypse instance;
	private Random rand;
	private World world;

	/**
	 * An instance containing a thread for ZA mobs spawning.
	 * 
	 * @param game The game to run the thread for
	 * @param mobspawn Whether or not to run the thread
	 */
	public MobSpawnThread(ZAGame game, boolean mobspawn) {
		this.game = game;
		this.i = 0;
		this.level = game.getLevel();
		this.instance = Ablockalypse.instance;
		this.players = game.getPlayers().size();
		this.rand = new Random();
		this.world = game.getSpawn().getWorld();
		// double m = players + (level * players);
		/*double m = 1.5 * (players / 2);
		if (players == 1)
			m = m + 1.0;*/
		/* Equation for getting mob spawn amount */
		double a1 = .0000000001, b1 = .02, c1 = 1, d1 = 0;
		double n = MathAssist.curve(players, a1, b1, c1, d1), m = 1.53;
		/* double a = .000005, b = .0001, c = 1, d = 4; *//* CURVE for curvefitVariable*/
		this.curvefitVariable = Math.round(MathAssist.line(level, m, n)); /* LINE for curverfitVariable */
		/* End equation */
		if (External.getYamlManager().getConfigurationData().DEBUG)
			System.out.println("[Ablockalypse] [DEBUG] Amt. of zombies in this level: (" + game.getName() + ") " + curvefitVariable);
		if (mobspawn)
			mobSpawn();
	}

	/**
	 * Cancels the thread.
	 */
	protected void cancel() {
		Bukkit.getScheduler().cancelTask(id);
	}

	/*
	 * Removes all data associated with this class.
	 */
	@SuppressWarnings("unused") @Override public void finalize() {
		for (Method m : this.getClass().getDeclaredMethods())
			m = null;
		for (Field f : this.getClass().getDeclaredFields())
			f = null;
	}

	/*
	 * Finds a good location to spawn a zombie, and does so.
	 */
	public void findSpawnLocation(Location l) {
		int chance = rand.nextInt(2);
		int x = rand.nextInt(16 - 10) + 10;
		int z = rand.nextInt(16 - 10) + 10;
		if (chance == 1) {
			runSpawn(x, z);
		} else {
			runSpawn(-x, -z);
		}
	}

	/*
	 * Spawn the entity, and should only be called by findSpawnLocation().
	 */
	private void runSpawn(int x, int z) {// TODO come up with a way to improve the distance for targetting players
		Player p = game.getRandomPlayer();
		Location ploc = p.getLocation();
		int x2 = ploc.getBlockX();
		// int y2 = ploc.getBlockY();
		int z2 = ploc.getBlockZ();
		Location loc = world.getHighestBlockAt((x2 + x), (z2 + z)).getLocation();
		Entity e = world.spawnEntity(loc, EntityType.ZOMBIE);
		GameUndead gu = new GameUndead((Zombie) e, game);
		if (game.getLevel() >= External.getYamlManager().getConfigurationData().doubleSpeedLevel)
			gu.increaseSpeed();
		// new PathfinderGoalMoveTowardsTarget((EntityCreature) Breakable.getNMSEntity(e), (float) 32, (float) 32);
		// Breakable.targetPlayer(e, p, loc, x, z, x2, y2, z2);
		game.addMobCount();
	}

	/**
	 * Spawns zombies each round, and if the round is a wolf round, spawns wolves as well.
	 */
	protected void mobSpawn() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (game.getLevel() == level && game.getRemainingPlayers() >= 1) {
					for (String s : game.getPlayers()) {
						Player p = Bukkit.getServer().getPlayer(s);
						if (i < curvefitVariable) {
							Location l = p.getLocation();
							findSpawnLocation(l);
							++i;
						} else {
							cancel();
						}
					}
					if (game.isWolfRound()) {
						for (String s : game.getPlayers()) {
							Player p = Bukkit.getPlayer(s);
							if (i < curvefitVariable) {
								Location l = p.getLocation();
								Entity e = p.getWorld().spawnEntity(l, EntityType.WOLF);// TODO wolves spawn 5 blocks from players
								new GameHellHound((Wolf) e, game);
								game.addMobCount();
								i++;
							} else {
								cancel();
							}
						}
					}
				} else {
					cancel();
				}
			}
		}, 60, 60);
	}
}
