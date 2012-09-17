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
import com.github.Ablockalypse.JamesNorris.Util.MathAssist;

public class MobSpawnThread {
	private ZAGame game;
	private int level, id;
	private Ablockalypse instance;
	private double curvefitVariable;
	private int i;

	/**
	 * An instance containing a thread for ZA mobs spawning.
	 * 
	 * @param game The game to run the thread for
	 * @param mobspawn Whether or not to run the thread
	 */
	public MobSpawnThread(ZAGame game, boolean mobspawn) {
		this.game = game;
		this.level = game.getLevel();
		this.instance = Ablockalypse.instance;
		/* Equation for getting mob spawn amount */
		double x = level + game.getPlayers().size();
		double a = 5.150202692369555E-4;
		double b = 0.009694737048487135;
		double c = 2.0740001246685478;
		double d = 1.5771190606123884;
		this.curvefitVariable = MathAssist.curve(x, a, b, c, d);
		/* End equation */
		if (mobspawn)
			mobSpawn();
	}

	/*
	 * Spawns zombies each round, and if the round is a wolf round, spawns wolves as well.
	 */
	protected void mobSpawn() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			public void run() {
				if (game.getLevel() == level) {
					for (String s : Data.spawners.keySet()) {
						Location l = Data.spawners.get(s);
						ZASpawner zas = Data.loadedspawners.get(l);
						if (i <= Math.round(curvefitVariable)) {
							game.addMob(zas, EntityType.ZOMBIE);
							i++;
						} else {
							cancel();
						}
					}
					if (game.isWolfRound()) {
						for (String s : game.getPlayers()) {
							Player p = Bukkit.getPlayer(s);
							if (i <= Math.round(curvefitVariable)) {
								Location l = p.getLocation();
								p.getWorld().spawnEntity(l, EntityType.WOLF);// TODO wolves spawn farther from players, and be able to tp to the player if path is blocked
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

	/*
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
