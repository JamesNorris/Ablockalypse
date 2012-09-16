package com.github.Ablockalypse.JamesNorris.Threading;

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

	/*
	 * The instance with all threads that should be run constantly while the plugin is running.
	 */
	public MainThreading(Ablockalypse instance) {
		this.instance = instance;
	}

	/*
	 * Checks for GameWolf instances and adds flames to them, making them hellhounds.
	 */
	public void wolfFlames() {
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
	public void barrier() {
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
}
