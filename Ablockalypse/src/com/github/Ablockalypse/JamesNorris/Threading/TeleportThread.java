package com.github.Ablockalypse.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;

public class TeleportThread {
	private Player player;
	private int time, id;
	private ZAPlayer zaplayer;
	private Ablockalypse instance;

	/**
	 * Creates an instance of the thread for teleporting a player.
	 * 
	 * @param zaplayer The player to countdown for, as a ZAPlayer instance
	 * @param time The time before the countdown stops
	 * @param countdown Whether or not to run the thread automatically
	 */
	public TeleportThread(ZAPlayer zaplayer, int time, boolean countdown) {
		this.zaplayer = zaplayer;
		this.time = time;
		this.player = zaplayer.getPlayer();
		this.instance = Ablockalypse.instance;
		if (countdown)
			countdown();
	}

	/*
	 * Counts down to teleport the player.
	 */
	protected void countdown() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			public void run() {
				if (time != 0)
					player.sendMessage(ChatColor.GRAY + "" + time + " seconds to teleport...");
				else {
					player.sendMessage(ChatColor.GRAY + "Teleporting to mainframe...");
					zaplayer.sendToMainframe();
					cancel();
				}
				--time;
			}
		}, 20, 20);
	}

	/*
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
