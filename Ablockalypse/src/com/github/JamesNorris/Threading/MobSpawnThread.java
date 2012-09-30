package com.github.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Manager.SpawnManager;
import com.github.JamesNorris.Util.MathAssist;

public class MobSpawnThread {
	private long curvefitVariable;
	private ZAGame game;
	private int i, id, level, players;
	private Ablockalypse instance;
	private SpawnManager sm;

	/**
	 * An instance containing a thread for ZA mobs spawning.
	 * 
	 * @param game The game to run the thread for
	 * @param mobspawn Whether or not to run the thread
	 */
	public MobSpawnThread(ZAGame game, boolean mobspawn) {
		this.game = game;
		this.i = 0;
		this.sm = game.getSpawnManager();
		this.level = game.getLevel();
		this.instance = Ablockalypse.instance;
		this.players = game.getPlayers().size();
		double a1 = .0000000001, b1 = .02, c1 = 1, d1 = 0;
		double n = MathAssist.curve(players, a1, b1, c1, d1), m = 1.53;
		this.curvefitVariable = Math.round(MathAssist.line(level, m, n));
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

	/**
	 * Spawns zombies each round, and if the round is a wolf round, spawns wolves as well.
	 */
	protected void mobSpawn() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (game.getSpawn() != null) {// Waiting for spawn to be loaded if not already.
					if (game.getLevel() == level && game.getRemainingPlayers() >= 1) {
						if (!game.isWolfRound()) {
							for (String s : game.getPlayers()) {
								Player p = Bukkit.getServer().getPlayer(s);
								if (i < curvefitVariable) {
									Location l = p.getLocation();
									Location loc = sm.findSpawnLocation(l, 16, 10);
									sm.gameSpawn(loc, EntityType.ZOMBIE);
									++i;
								} else {
									cancel();
								}
							}
						} else if (game.isWolfRound()) {
							for (String s : game.getPlayers()) {
								Player p = Bukkit.getPlayer(s);
								if (i < curvefitVariable) {
									Location l = p.getLocation();
									Location loc = sm.findSpawnLocation(l, 7, 4);
									sm.gameSpawn(loc, EntityType.WOLF);
									++i;
								} else {
									cancel();
								}
							}
						}
					} else {
						cancel();
					}
				}
			}
		}, 60, 60);
	}
}
