package com.github.Ablockalypse.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGameBase;
import com.github.Ablockalypse.JamesNorris.Implementation.GameBlockSpawner;
import com.github.Ablockalypse.JamesNorris.Util.MathAssist;

public class MobSpawnThread {
	private long curvefitVariable;
	private ZAGameBase game;
	private int i;
	private int id;
	private Ablockalypse instance;
	private int level;
	private int players;

	/**
	 * An instance containing a thread for ZA mobs spawning.
	 * 
	 * @param game The game to run the thread for
	 * @param mobspawn Whether or not to run the thread
	 */
	public MobSpawnThread(ZAGameBase game, boolean mobspawn) {
		this.game = game;
		level = game.getLevel();
		instance = Ablockalypse.instance;
		players = game.getPlayers().size();
		/* Equation for getting mob spawn amount */
		double a1 = .0000000001, b1 = .02, c1 = 1, d1 = 0;
		double n = MathAssist.curve(players, a1, b1, c1, d1), m = 1.53;
		/* double a = .000005, b = .0001, c = 1, d = 4; CURVE for curvefitVariable*/
		curvefitVariable = Math.round(MathAssist.line(level, m, n)); /* LINE for curverfitVariable */
		/* End equation */
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

	/**
	 * Spawns zombies each round, and if the round is a wolf round, spawns wolves as well.
	 */
	protected void mobSpawn() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (game.getLevel() == level) {
					for (String s : Data.spawners.keySet()) {
						Location l = Data.spawners.get(s);
						GameBlockSpawner zas = Data.loadedspawners.get(l);
						if (i <= curvefitVariable) {
							game.addMob(zas, EntityType.ZOMBIE);
							i++;
						} else {
							cancel();
						}
					}
					if (game.isWolfRound()) {
						for (String s : game.getPlayers()) {
							Player p = Bukkit.getPlayer(s);
							if (i <= curvefitVariable) {
								Location l = p.getLocation();
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
		}, 60, 60);
	}
}
