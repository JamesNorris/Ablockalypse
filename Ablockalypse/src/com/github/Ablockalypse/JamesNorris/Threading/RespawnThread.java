package com.github.Ablockalypse.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;

public class RespawnThread {
	private final int level;
	private int id;
	private final ZAPlayer player;
	private final Ablockalypse instance;

	/**
	 * The thread used for respawning the player.
	 * 
	 * @param player The player to wait for
	 * @param level The level the game is currently on
	 * @param waitrespawn Whether or not to automatically run this thread
	 */
	public RespawnThread(final ZAPlayer player, final int level, final boolean waitrespawn) {
		this.player = player;
		this.level = level;
		instance = Ablockalypse.instance;
		if (waitrespawn)
			waitToRespawn();
	}

	/**
	 * Waits for the next level, then respawns the player.
	 */
	protected void waitToRespawn() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (player.getGame().getLevel() > level) {
					player.sendToMainframe();
					cancel();
				}
			}
		}, 20, 20);
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
