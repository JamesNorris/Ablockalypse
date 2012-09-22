package com.github.Ablockalypse.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.Barrier;
import com.github.Ablockalypse.JamesNorris.Implementation.GameWolf;
import com.github.Ablockalypse.JamesNorris.Implementation.GameZombie;
import com.github.Ablockalypse.JamesNorris.Util.Square;

public class MainThreading {
	private final Ablockalypse instance;
	private int id1, id2;

	/**
	 * The instance with all threads that should be run constantly while the plugin is running.
	 * 
	 * @param instance The Ablockalypse instance to run this thread for
	 * @param wolf Whether or not to run the wolfFlames thread
	 * @param barrier Whether or not to run the barrier thread
	 */
	public MainThreading(final Ablockalypse instance, final boolean wolf, final boolean barrier) {
		this.instance = instance;
		if (wolf)
			wolfFlames();
		if (barrier)
			barrier();
	}

	/**
	 * Checks for GameWolf instances and adds flames to them, making them hellhounds.
	 */
	protected void wolfFlames() {
		id1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				for (final GameWolf f : Data.wolves) {
					if (!f.getWolf().isDead())
						f.addEffect();
				}
			}
		}, 20, 20);
	}

	/**
	 * Checks for GameZombie instances, and checks if they are in a Barrier area. If they are, the Barrier is broken.
	 */
	protected void barrier() {
		id2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				for (final GameZombie gz : Data.zombies) {
					for (final Barrier b : Data.barrierpanels.keySet()) {
						final Square s = Data.findBarrierSquare(b, b.getCenter(), 3);
						for (final Location l : s.getLocations()) {
							if (gz.getZombie().getLocation() == l) {
								b.breakBarrier();
							}
						}
					}
				}
			}
		}, 40, 40);
	}

	/**
	 * Cancels both tasks.
	 */
	protected void cancelAll() {
		final BukkitScheduler bgs = Bukkit.getScheduler();
		bgs.cancelTask(id1);
		bgs.cancelTask(id2);
	}

	/**
	 * Cancels the wolf flames task.
	 */
	protected void cancelFlames() {
		Bukkit.getScheduler().cancelTask(id1);
	}

	/**
	 * Cancels the barrier break task.
	 */
	protected void cancelBarrier() {
		Bukkit.getScheduler().cancelTask(id2);
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
