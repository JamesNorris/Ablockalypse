package com.github.Ablockalypse.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGame;
import com.github.Ablockalypse.JamesNorris.Implementation.ZASpawner;
import com.github.Ablockalypse.JamesNorris.Manager.TickManager;
import com.github.Ablockalypse.JamesNorris.Util.MathAssist;

public class MobSpawnThread {
	private final ZAGame game;
	private final int level;
	private int id;
	private int i;
	private final int players;
	private final Ablockalypse instance;
	private final long curvefitVariable;
	private TickManager tm;

	/**
	 * An instance containing a thread for ZA mobs spawning.
	 * 
	 * @param game The game to run the thread for
	 * @param mobspawn Whether or not to run the thread
	 */
	public MobSpawnThread(final ZAGame game, final boolean mobspawn) {
		this.game = game;
		this.tm = Ablockalypse.getMaster().getTickManager();
		level = game.getLevel();
		instance = Ablockalypse.instance;
		players = game.getPlayers().size();
		/* Equation for getting mob spawn amount */
		final double a1 = .0000000001, b1 = .02, c1 = 1, d1 = 0;
		final double n = MathAssist.curve(players, a1, b1, c1, d1), m = 1.53;
		/* double a = .000005, b = .0001, c = 1, d = 4; CURVE for curvefitVariable*/
		curvefitVariable = Math.round(MathAssist.line(level, m, n)); /* LINE for curverfitVariable */
		/* End equation */
		if (mobspawn)
			mobSpawn();
	}

	/**
	 * Spawns zombies each round, and if the round is a wolf round, spawns wolves as well.
	 */
	protected void mobSpawn() {
		int i2 = tm.getAdaptedRate() * 3;
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (game.getLevel() == level) {
					for (final String s : Data.spawners.keySet()) {
						final Location l = Data.spawners.get(s);
						final ZASpawner zas = Data.loadedspawners.get(l);
						if (i <= curvefitVariable) {
							game.addMob(zas, EntityType.ZOMBIE);
							i++;
						} else {
							cancel();
						}
					}
					if (game.isWolfRound()) {
						for (final String s : game.getPlayers()) {
							final Player p = Bukkit.getPlayer(s);
							if (i <= curvefitVariable) {
								final Location l = p.getLocation();
								p.getWorld().spawnEntity(l, EntityType.WOLF);// TODO wolves spawn farther from players, and be able to tp to the player if path is blocked
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
		}, i2, i2);
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
}
