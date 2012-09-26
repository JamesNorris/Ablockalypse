package com.github.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import com.github.Ablockalypse;
import com.github.JamesNorris.Interface.ZAGame;

public class NextLevelThread {
	private ZAGame game;
	private int id, counter;
	private Ablockalypse instance;

	/**
	 * The thread for checking for next level, depending on remaining mobs.
	 * 
	 * @param game The game to run the thread for
	 * @param nextlevel Whether or not to run the thread automatically
	 */
	public NextLevelThread(ZAGame game, boolean nextlevel) {
		this.game = game;
		this.instance = Ablockalypse.instance;
		this.counter = 10;
		if (nextlevel)
			waitForNextLevel();
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
	 * Waits for the mobs to all be killed, then starts the next level.
	 */
	protected void waitForNextLevel() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (game.getRemainingMobs() <= 0) {
					--counter;
					if (counter == 0) {
						game.nextLevel();
						cancel();
					}
				}
			}
		}, 20, 20);
	}
}
