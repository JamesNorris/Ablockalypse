package com.github.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Manager.SoundManager.ZASound;

public class NextLevelThread {
	private ZAGame game;
	private int id, counter, original;
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
		this.original = counter;
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
					if (counter == original - 1) {
						for (String s : game.getPlayers()) {
							ZAPlayer zap = Data.findZAPlayer(Bukkit.getPlayer(s), game.getName());
							zap.getSoundManager().generateSound(ZASound.PREV_LEVEL);
						}
					}
					if (counter == 0) {
						game.nextLevel();
						for (String s : game.getPlayers()) {
							ZAPlayer zap = Data.findZAPlayer(Bukkit.getPlayer(s), game.getName());
							zap.getSoundManager().generateSound(ZASound.NEXT_LEVEL);
						}
						cancel();
					}
				}
			}
		}, 20, 20);
	}
}
