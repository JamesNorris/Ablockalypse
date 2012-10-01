package com.github.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Interface.ZAPlayer;

public class TeleportThread {
	private ConfigurationData cd;
	private Ablockalypse instance;
	private Location loc;
	private Player player;
	private int time, id;
	private ZAPlayer zaplayer;

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
		player = zaplayer.getPlayer();
		instance = Ablockalypse.instance;
		cd = External.ym.getConfigurationData();
		this.loc = zaplayer.getPlayer().getLocation();
		if (countdown)
			countdown();
	}

	/**
	 * Cancels the thread.
	 */
	protected void cancel() {
		Bukkit.getScheduler().cancelTask(id);
	}

	/**
	 * Counts down to teleport the player.
	 */
	protected void countdown() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (time != 0) {
					player.sendMessage(ChatColor.GRAY + "" + time + " seconds to teleport...");
					if (!sameLocation()) {
						cancel();
						player.sendMessage(ChatColor.GRAY + "Teleportation cancelled!");
					}
					if (time == 1 && cd.effects)
						zaplayer.getPlayer().getWorld().playEffect(zaplayer.getPlayer().getLocation(), Effect.SMOKE, 1);
					--time;
				} else if (time <= 0) {
					zaplayer.sendToMainframe("Teleport");
					if (cd.effects)
						zaplayer.getPlayer().getWorld().playEffect(zaplayer.getPlayer().getLocation(), Effect.SMOKE, 1);
					cancel();
				}
			}
		}, 20, 20);
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

	/*
	 * Checks if the player is in roughly the same location as they were when they started the thread.
	 */
	private boolean sameLocation() {
		if (player.getLocation().getBlockX() == loc.getBlockX() && player.getLocation().getBlockY() == loc.getBlockY() && player.getLocation().getBlockZ() == loc.getBlockZ()) {
			return true;
		}
		return false;
	}
}
