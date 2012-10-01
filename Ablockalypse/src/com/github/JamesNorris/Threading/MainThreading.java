package com.github.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameHellHound;
import com.github.JamesNorris.Implementation.GameUndead;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Util.Square;

public class MainThreading {
	private int id1, id2, id3, id4, id5;
	private Ablockalypse instance;

	/**
	 * The instance with all threads that should be run constantly while the plugin is running.
	 * 
	 * @param instance The Ablockalypse instance to run this thread for
	 * @param wolf Whether or not to run the wolfFlames thread
	 * @param barrier Whether or not to run the barrier thread
	 */
	public MainThreading(Ablockalypse instance, boolean wolf, boolean barrier, boolean exp, boolean slime, boolean retarget) {
		this.instance = instance;
		if (wolf)
			wolfFlames();
		if (barrier)
			barrier();
		if (exp)
			exp();
		if (slime)
			slime();
		if (retarget)
			retarget();
	}

	/**
	 * Checks for GameZombie instances, and checks if they are in a Barrier area. If they are, the Barrier is broken.
	 */
	public void barrier() {
		id2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (Data.undead != null && Data.barrierpanels != null) {
					for (GameUndead gz : Data.undead) {
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
	 * Cancels all tasks for the mainthreading class.
	 */
	public void cancelAll() {
		BukkitScheduler bgs = Bukkit.getScheduler();
		bgs.cancelTask(id1);
		bgs.cancelTask(id2);
		bgs.cancelTask(id3);
		bgs.cancelTask(id4);
		bgs.cancelTask(id5);
	}

	/**
	 * Cancels the barrier break task.
	 */
	public void cancelBarrier() {
		Bukkit.getScheduler().cancelTask(id2);
	}

	/**
	 * Cancels the exp obtain task.
	 */
	public void cancelExp() {
		Bukkit.getScheduler().cancelTask(id3);
	}

	/**
	 * Cancels the wolf flames task.
	 */
	public void cancelFlames() {
		Bukkit.getScheduler().cancelTask(id1);
	}

	/**
	 * Cancels the mob removal task.
	 */
	public void cancelRemoval() {
		Bukkit.getScheduler().cancelTask(id4);
	}

	/**
	 * Cancels the retarget task.
	 */
	public void cancelRetarget() {
		Bukkit.getScheduler().cancelTask(id5);
	}

	/**
	 * Checks if players have gained exp, and if so, removes the exp.
	 */
	public void exp() {
		id3 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			public void run() {
				for (Player p : Data.players.keySet()) {
					if (p.getExp() != 0) {
						int prev = p.getLevel();// TODO just cancel an item event?
						p.setExp(0);
						p.setLevel(prev);
					}
				}
			}
		}, 60, 60);
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
	 * Changes targets for
	 */
	public void retarget() {
		id5 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			public void run() {
				for (ZAMob zam : Data.getZAMobs()) {
					if (zam.getTarget() == null) {
						zam.setTarget(zam.getGame().getRandomPlayer());
					}
				}
			}
		}, 60, 60);
	}

	/**
	 * Starts a thread that prevents slimes from going near players without dying.
	 */
	public void slime() {
		id4 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			public void run() {
				for (Player p : Data.players.keySet()) {
					for (Entity e : p.getNearbyEntities(32, 32, 32)) {
						if (e != null && (e.getType() == EntityType.SLIME || !Data.isZAMob(e)) && !(e instanceof Player)) {
							e.remove();
						}
					}
				}
			}
		}, 60, 60);
	}

	/**
	 * Checks for GameWolf instances and adds flames to them, making them hellhounds.
	 */
	public void wolfFlames() {
		id1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (Data.hellhounds != null) {
					for (GameHellHound f : Data.hellhounds) {
						if (!f.getWolf().isDead())
							f.addEffect();
					}
				}
			}
		}, 20, 20);
	}
}
