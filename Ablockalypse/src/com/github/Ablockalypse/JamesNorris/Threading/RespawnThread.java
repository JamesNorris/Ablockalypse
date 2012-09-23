package com.github.Ablockalypse.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;
import com.github.Ablockalypse.JamesNorris.Manager.TickManager;

public class RespawnThread {
	private final int level;
	private int id;
	private final ZAPlayer player;
	private final Ablockalypse instance;
	private TickManager tm;

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
		this.tm = Ablockalypse.getMaster().getTickManager();
		instance = Ablockalypse.instance;
		if (waitrespawn)
			waitToRespawn();
	}

	/**
	 * Waits for the next level, then respawns the player.
	 */
	protected void waitToRespawn() {
		int i = tm.getAdaptedRate();
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (player.getGame().getLevel() > level) {
					player.sendToMainframe();
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
