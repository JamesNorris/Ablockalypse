package com.github.Ablockalypse.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.GameBarrier;
import com.github.Ablockalypse.JamesNorris.Implementation.GameHellHound;
import com.github.Ablockalypse.JamesNorris.Implementation.GameUndead;
import com.github.Ablockalypse.JamesNorris.Util.Square;

public class MainThreading {
	private int id1, id2;
	private Ablockalypse instance;

	/**
	 * The instance with all threads that should be run constantly while the plugin is running.
	 * 
	 * @param instance The Ablockalypse instance to run this thread for
	 * @param wolf Whether or not to run the wolfFlames thread
	 * @param barrier Whether or not to run the barrier thread
	 */
	public MainThreading(Ablockalypse instance, boolean wolf, boolean barrier) {
		this.instance = instance;
		if (wolf)
			wolfFlames();
		if (barrier)
			barrier();
	}

	/**
	 * Checks for GameZombie instances, and checks if they are in a Barrier area. If they are, the Barrier is broken.
	 */
	public void barrier() {
		id2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (Data.zombies != null && Data.barrierpanels != null) {
					for (GameUndead gz : Data.zombies) {
						for (GameBarrier b : Data.barrierpanels.keySet()) {
							Square s = Data.findBarrierSquare(b, b.getCenter(), 3);
							for (Location l : s.getLocations()) {
								if (gz.getZombie().getLocation() == l) {
									b.breakBarrier();
								}
							}
						}
					}
				}
			}
		}, 60, 60);
	}

	/**
	 * Cancels both tasks.
	 */
	public void cancelAll() {
		BukkitScheduler bgs = Bukkit.getScheduler();
		bgs.cancelTask(id1);
		bgs.cancelTask(id2);
	}

	/**
	 * Cancels the barrier break task.
	 */
	public void cancelBarrier() {
		Bukkit.getScheduler().cancelTask(id2);
	}

	/**
	 * Cancels the wolf flames task.
	 */
	public void cancelFlames() {
		Bukkit.getScheduler().cancelTask(id1);
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
	 * Checks for GameWolf instances and adds flames to them, making them hellhounds.
	 */
	public void wolfFlames() {
		id1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (Data.wolves != null) {
					for (GameHellHound f : Data.wolves) {
						if (!f.getWolf().isDead())
							f.addEffect();
					}
				}
			}
		}, 20, 20);
	}
}
