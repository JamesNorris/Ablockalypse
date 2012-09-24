package com.github.Ablockalypse.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Interface.ZAPlayer;

public class LastStandThread {
	private int id;
	private Player player;
	private ZAPlayer zap;

	/**
	 * Creates a new LastStandThread instance.
	 * 
	 * @param zap The ZAPlayer to hurt
	 * @param autorun
	 */
	public LastStandThread(ZAPlayer zap, boolean autorun) {
		this.zap = zap;
		this.player = zap.getPlayer();
		if (autorun)
			die();
	}

	/**
	 * Cancels the thread.
	 */
	protected void cancel() {
		Bukkit.getScheduler().cancelTask(id);
	}

	/**
	 * Slowly kills the player.
	 */
	protected void die() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
			public void run() {
				if (zap.isInLastStand() && !player.isDead()) {
					player.setHealth(player.getHealth() - 1);
				} else {
					cancel();
				}
			}
		}, 60, 60);
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
