package com.github.Ablockalypse.JamesNorris.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class ControlledEffect {
	private final World world;
	private final int radius, direction, X, Y, Z;
	private int id;
	private int interval;
	private JavaPlugin plugin;
	private final Effect effect;
	private boolean running;
	private final Location center;

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
	public ControlledEffect(final World world, final Effect effect, final int radius, final int direction, final Location center, final boolean autorun) {
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
	public ControlledEffect(final JavaPlugin plugin, final World world, final Effect effect, final int radius, final int direction, final Location center, final boolean autorun, final boolean scheduler, final int interval) {
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
	 * Creates a single effect using this ControlledEffect instance.
	 */
	protected void effect() {
		if (radius > 1) {
			for (int x = -radius; x <= radius; ++x) {
				for (final int y = -radius; y <= radius; ++x) {
					for (int z = -radius; z <= radius; ++z) {
						final Location loc = world.getBlockAt(X + x, Y + y, Z + z).getLocation();
						world.playEffect(loc, effect, direction);
					}
				}
			}
		} else
			world.playEffect(center, effect, direction);
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

	/**
	 * Stops the scheduler.
	 */
	protected void cancel() {
		Bukkit.getScheduler().cancelTask(id);
		running = false;
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
}
