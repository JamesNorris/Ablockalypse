package com.github.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.EffectUtil.ZAEffect;

public class TeleportThread {
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
		loc = zaplayer.getPlayer().getLocation();
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
				zaplayer.setTeleporting(true);
				if (time != 0) {
					if (!sameLocation()) {
						cancel();
						player.sendMessage(ChatColor.GRAY + "Teleportation cancelled!");
						zaplayer.setTeleporting(false);
						return;
					} else {
						player.sendMessage(ChatColor.GRAY + "" + time + " seconds to teleport...");
						--time;
					}
				} else if (time <= 0) {
					EffectUtil.generateControlledEffect(player.getLocation(), ZAEffect.SMOKE, 1);
					zaplayer.sendToMainframe("Teleport");
					EffectUtil.generateControlledEffect(player.getLocation(), ZAEffect.SMOKE, 1);
					zaplayer.setTeleporting(false);
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
		if (player.getLocation().getBlockX() == loc.getBlockX() && player.getLocation().getBlockY() == loc.getBlockY() && player.getLocation().getBlockZ() == loc.getBlockZ())
			return true;
		return false;
	}
}
