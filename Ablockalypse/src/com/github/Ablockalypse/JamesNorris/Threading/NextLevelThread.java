package com.github.Ablockalypse.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGame;
import com.github.Ablockalypse.JamesNorris.Manager.TickManager;

public class NextLevelThread {
	private final ZAGame game;
	private final Ablockalypse instance;
	private int id;
	private TickManager tm;

	/**
	 * The thread for checking for next level, depending on remaining mobs.
	 * 
	 * @param game The game to run the thread for
	 * @param nextlevel Whether or not to run the thread automatically
	 */
	public NextLevelThread(final ZAGame game, final boolean nextlevel) {
		this.game = game;
		this.tm = Ablockalypse.getMaster().getTickManager();
		instance = Ablockalypse.instance;
		if (nextlevel)
			waitForNextLevel();
	}

	/**
	 * Waits for the mobs to all be killed, then starts the next level.
	 */
	protected void waitForNextLevel() {
		int i = tm.getAdaptedRate();
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (game.getRemainingMobs() <= 0) {
					game.nextLevel();
					cancel();
				}
			}
		}, i, i);
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
