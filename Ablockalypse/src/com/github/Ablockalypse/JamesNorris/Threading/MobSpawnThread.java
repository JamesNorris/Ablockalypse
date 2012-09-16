package com.github.Ablockalypse.JamesNorris.Threading;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGame;
import com.github.Ablockalypse.JamesNorris.Implementation.ZASpawner;

public class MobSpawnThread {
	private ZAGame game;
	private int level, mobs, id;
	private Ablockalypse instance;

	/*
	 * An instance containing a thread for ZA mobs spawning.
	 */
	public MobSpawnThread(ZAGame game) {
		this.game = game;
		this.level = game.getLevel();
		this.mobs = game.getRemainingMobs();
		this.instance = Ablockalypse.instance;
	}

	/*
	 * Spawns zombies each round, and if the round is a wolf round, spawns wolves as well.
	 */
	public void mobSpawn() {
		mobs = game.getRemainingMobs();
		level = game.getLevel();
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			public void run() {
				if (game.getLevel() == level && mobs != 0) {// make into a while?
					for (String s : Data.spawners.keySet()) {
						Location l = Data.spawners.get(s);
						ZASpawner zas = Data.loadedspawners.get(l);
						for (int i = 0; i <= (game.getPlayers().size() + (1.05 * level)); i++)
							// TODO make 1.05 into the mobIncrease config variable
							game.addMob(zas, EntityType.ZOMBIE);
						--mobs;
					}
					if (game.isWolfRound()) {
						for (String s : game.getPlayers()) {
							Player p = Bukkit.getPlayer(s);
							Location l = p.getLocation();
							for (int i = 0; i < (s.length() + (1.05 * level)); i++)
								p.getWorld().spawnEntity(l, EntityType.WOLF);// TODO wolves spawn farther from players, and be able to tp to the player if path is blocked
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
	private void cancel() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
