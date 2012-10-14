package com.github.JamesNorris.Threading;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.Ablockalypse;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameHellHound;
import com.github.JamesNorris.Implementation.GameUndead;

public class MainThreading {
	private int id1, id2, id4;
	private Ablockalypse instance;
	private ConfigurationData cd;

	/**
	 * The instance with all threads that should be run constantly while the plugin is running.
	 * 
	 * @param instance The Ablockalypse instance to run this thread for
	 * @param wolf Whether or not to run the wolfFlames thread
	 * @param barrier Whether or not to run the barrier thread
	 * @param clearmobs Whether or not to run the mob clearing thread
	 */
	public MainThreading(Plugin instance, boolean wolf, boolean barrier, boolean clearmobs) {
		this.instance = (Ablockalypse) instance;
		this.cd = External.getYamlManager().getConfigurationData();
		if (wolf)
			wolfFlames();
		if (barrier)
			barrier();
		if (clearmobs && cd.clearmobs)
			clearMobs();
	}

	/**
	 * Checks for zombie instances, and checks if they are in a Barrier area. If they are, the Barrier is broken.
	 */
	public void barrier() {
		id2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				for (GameBarrier bg : Data.barrierpanels.keySet()) {
					for (GameUndead gu : Data.undead) {
						if (bg.withinRadius(gu.getEntity()) && !bg.isBroken())
							bg.breakBarrier((Creature) gu.getZombie());
					}
					for (GameHellHound ghh : Data.hellhounds) {
						if (bg.withinRadius(ghh.getEntity()) && !bg.isBroken())
							bg.breakBarrier((Creature) ghh.getWolf());
					}
				}
			}
		}, 20, 20);
	}

	/**
	 * Cancels all tasks for the mainthreading class.
	 */
	public void cancelAll() {
		BukkitScheduler bgs = Bukkit.getScheduler();
		bgs.cancelTask(id1);
		bgs.cancelTask(id2);
		bgs.cancelTask(id4);
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

	/**
	 * Cancels the mob removal task.
	 */
	public void cancelRemoval() {
		Bukkit.getScheduler().cancelTask(id4);
	}

	/**
	 * Starts a thread that prevents slimes from going near players without dying.
	 */
	public void clearMobs() {
		if (cd.clearmobs) {
			id4 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
				@Override public void run() {
					for (Player p : Data.players.keySet())
						for (Entity e : p.getNearbyEntities(32, 32, 32))
							if (e != null && (e.getType() == EntityType.SLIME || !Data.isZAMob(e)) && !(e instanceof Player))
								e.remove();
				}
			}, 60, 60);
		}
	}

	/**
	 * Checks for GameWolf instances and adds flames to them, making them hellhounds.
	 */
	public void wolfFlames() {
		id1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (Data.hellhounds != null)
					for (GameHellHound f : Data.hellhounds)
						if (!f.getWolf().isDead())
							f.addEffect();
			}
		}, 20, 20);
	}
}
