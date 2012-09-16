package com.github.Ablockalypse.JamesNorris.Threading;

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

	/*
	 * Creates an instance of the thread for teleporting a player.
	 */
	public TeleportThread(ZAPlayer zaplayer, int time) {
		this.zaplayer = zaplayer;
		this.time = time;
		this.player = zaplayer.getPlayer();
		this.instance = Ablockalypse.instance;
	}

	/*
	 * Counts down to teleport the player.
	 */
	public void countdown() {
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
	private void cancel() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
