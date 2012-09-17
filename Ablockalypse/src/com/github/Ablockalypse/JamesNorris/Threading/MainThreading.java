package com.github.Ablockalypse.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.Barrier;
import com.github.Ablockalypse.JamesNorris.Implementation.GameWolf;
import com.github.Ablockalypse.JamesNorris.Implementation.GameZombie;
import com.github.Ablockalypse.JamesNorris.Util.Square;

public class MainThreading {
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

	/*
	 * Checks for GameWolf instances and adds flames to them, making them hellhounds.
	 */
	protected void wolfFlames() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			public void run() {
				for (GameWolf f : Data.wolves) {
					f.addEffect();
				}
			}
		}, 20, 20);
	}

	/*
	 * Checks for GameZombie instances, and checks if they are in a Barrier area. If they are, the Barrier is broken.
	 */
	protected void barrier() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			public void run() {
				for (GameZombie gz : Data.zombies) {
					for (Barrier b : Data.barrierpanels.keySet()) {
						Square s = Data.findBarrierSquare(b, b.getCenter(), 3);
						for (Location l : s.getLocations()) {
							if (gz.getZombie().getLocation() == l) {
								b.breakBarrier();
							}
						}
					}
				}
			}
		}, 40, 40);
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
