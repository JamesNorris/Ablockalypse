package com.github.Ablockalypse.JamesNorris.Threading;

import org.bukkit.Bukkit;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;

public class RespawnThread {
	private int level, id;
	private ZAPlayer player;
	private Ablockalypse instance;

	/*
	 * The thread used for respawning the player.
	 */
	public RespawnThread(ZAPlayer player, int level) {
		this.player = player;
		this.level = level;
		this.instance = Ablockalypse.instance;
	}

	/*
	 * Waits for the next level, then respawns the player.
	 */
	public void waitToRespawn() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			public void run() {
				if (player.getGame().getLevel() > level) {
					player.sendToMainframe();
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
