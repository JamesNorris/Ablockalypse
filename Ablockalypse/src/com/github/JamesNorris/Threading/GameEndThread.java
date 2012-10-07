package com.github.JamesNorris.Threading;

import org.bukkit.Bukkit;

import com.github.Ablockalypse;
import com.github.JamesNorris.Interface.ZAGame;

public class GameEndThread {
	private int time;
	private ZAGame game;

	/**
	 * Waits to end the given game after the given time.
	 * 
	 * @param game The game to end
	 * @param time The time to wait to end the game
	 * @param autorun Whether or not to run the thread automatically
	 */
	public GameEndThread(ZAGame game, int time, boolean autorun) {
		this.game = game;
		this.time = time;
		if (autorun)
			endGame();
	}

	/**
	 * Schedules the delayed task to end the game.
	 */
	public void endGame() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Ablockalypse.instance, new Runnable() {
			@Override public void run() {
				if (game.getRemainingPlayers() == 0)
					game.endGame();
			}
		}, time);
	}
}
