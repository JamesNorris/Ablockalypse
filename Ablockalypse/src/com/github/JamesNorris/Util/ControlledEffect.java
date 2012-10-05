package com.github.JamesNorris.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class ControlledEffect {
	private Location center;
	private Effect effect;
	private int id;
	private int interval;
	private JavaPlugin plugin;
	private int radius, direction, X, Y, Z;
	private boolean running;
	private World world;

	/**
	 * Creates a new controlled effect WITH the scheduler.
	 * 
	 * @param plugin The plugin to run this from
	 * @param world The world to play the effect in
	 * @param effect The type of effect to play
	 * @param radius The radius of the effect
	 * @param direction The direction of the effect
	 * @param center The center of the effect
	 * @param autorun Whether or not to autorun the effect when the instance is created
	 * @param scheduler Whether or not to use the scheduler
	 * @param interval The amout of time to wait between playing the effect
	 */
	public ControlledEffect(JavaPlugin plugin, World world, Effect effect, int radius, int direction, Location center, boolean autorun, boolean scheduler, int interval) {
		this.interval = interval;
		this.plugin = plugin;
		this.world = world;
		this.radius = radius;
		this.direction = direction;
		this.effect = effect;
		this.center = center;
		X = center.getBlockX();
		Y = center.getBlockY();
		Z = center.getBlockZ();
		if (autorun)
			effect();
		if (scheduler)
			schedule();
	}

	/**
	 * Creates a new controlled effect WITHOUT the scheduler.
	 * 
	 * @param plugin The plugin to run this from
	 * @param world The world to play the effect in
	 * @param effect The type of effect to play
	 * @param radius The radius of the effect
	 * @param direction The direction of the effect
	 * @param center The center of the effect
	 * @param autorun Whether or not to autorun the effect when the instance is created
	 */
	public ControlledEffect(World world, Effect effect, int radius, int direction, Location center, boolean autorun) {
		this.world = world;
		this.radius = radius;
		this.direction = direction;
		this.effect = effect;
		this.center = center;
		X = center.getBlockX();
		Y = center.getBlockY();
		Z = center.getBlockZ();
		if (autorun)
			effect();
	}

	/**
	 * Stops the scheduler.
	 */
	protected void cancel() {
		Bukkit.getScheduler().cancelTask(id);
		running = false;
	}

	/**
	 * Creates a single effect using this ControlledEffect instance.
	 */
	protected void effect() {
		if (radius > 1)
			for (int x = -radius; x <= radius; ++x)
				for (int y = -radius; y <= radius; ++x)
					for (int z = -radius; z <= radius; ++z) {
						Location loc = world.getBlockAt(X + x, Y + y, Z + z).getLocation();
						world.playEffect(loc, effect, direction);
					}
		else
			world.playEffect(center, effect, direction);
	}

	/**
	 * Clears all data from this instance.
	 */
	@SuppressWarnings("unused") @Override public void finalize() {
		for (Method m : this.getClass().getDeclaredMethods())
			m = null;
		for (Field f : this.getClass().getDeclaredFields())
			f = null;
	}

	/**
	 * Schedules a new set of effects with this instance.
	 */
	protected void schedule() {
		if (!running) {
			running = true;
			id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				@Override public void run() {
					effect();
				}
			}, interval, interval);
		}
	}
}
