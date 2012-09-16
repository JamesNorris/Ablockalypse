package com.github.Ablockalypse.JamesNorris.Threading;

import org.bukkit.Bukkit;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGame;

public class NextLevelThread {
	private ZAGame game;
	private Ablockalypse instance;
	private int id;

	/*
	 * The thread for checking for next level, depending on remaining mobs.
	 */
	public NextLevelThread(ZAGame game) {
		this.game = game;
		this.instance = Ablockalypse.instance;
	}

	/*
	 * Waits for the mobs to all be killed, then starts the next level.
	 */
	public void waitForNextLevel() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			public void run() {
				if (game.getRemainingMobs() <= 0) {
					game.nextLevel();
					cancel();
				}
			}
		}, 20, 20);
	}

	/*
	 * Cancels the thread.
	 */
	private void cancel() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
