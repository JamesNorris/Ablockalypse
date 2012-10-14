package com.github.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.ZAPlayer;

public class RespawnThread {
	private Player player;
	private int time, id, level;
	private ZAPlayer zap;

	/**
	 * Creates a new RespawnThread instance.
	 * 
	 * @param player The player to wait for
	 * @param time The time to count down
	 * @param autorun Whether or not to automatically run the thread
	 */
	public RespawnThread(Player player, int time, boolean autorun) {
		this.player = player;
		this.time = time;
		this.zap = Data.getZAPlayer(player);
		this.level = zap.getGame().getLevel();
		if (autorun)
			waitToRespawn();
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
	 * Counts down for the player to respawn.
	 */
	protected void waitToRespawn() {
		player.sendMessage(ChatColor.GRAY + "You will respawn at the beginning of the next level.");
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
			@Override public void run() {
				if (zap.getGame().getLevel() > level) {
					if (time == 0) {
						ZAPlayer zap = Data.players.get(player);
						if (zap.getGame() == null)
							cancel();
						zap.sendToMainframe("Respawn");
						zap.setLimbo(false);
						cancel();
					} else
						player.sendMessage(ChatColor.GRAY + "Waiting to respawn... " + time);
					--time;
				}
			}
		}, 20, 20);
	}
}
