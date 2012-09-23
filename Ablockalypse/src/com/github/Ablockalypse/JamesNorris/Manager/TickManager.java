package com.github.Ablockalypse.JamesNorris.Manager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.Ablockalypse.Ablockalypse;

public class TickManager {
	private JavaPlugin plugin;
	long before, after;
	double offsetticks;

	/**
	 * Creates a new TickManager instance, which is used to adaptively find the ticks to use in shcedulers.
	 * 
	 * @param plugin The JavaPlugin instance to associate this instance with
	 */
	public TickManager(Ablockalypse plugin) {
		this.plugin = plugin;
	}

	/**
	 * Gets the adapted ticks per second rate for the server.
	 * 
	 * @return The adapted TPS rate
	 */
	public int getAdaptedRate() {
		before = System.currentTimeMillis();
		after = 0;
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				after = System.currentTimeMillis();
			}
		}, 20);
		offsetticks = (double) before - after / 50;
		int adapted = (int) offsetticks + 20;
		return adapted;
	}
}
