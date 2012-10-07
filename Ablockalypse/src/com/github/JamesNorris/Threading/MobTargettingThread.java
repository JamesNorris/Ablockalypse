package com.github.JamesNorris.Threading;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.PathEntity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;

public class MobTargettingThread {
	private final Plugin plugin;
	private Player p;
	private Creature c;
	private int id;
	private boolean hasTarget = false;
	private float speed = 0.2F;
	private float radius = 32.0F;

	/**
	 * Creates a new mobtargetter, that can target specific locations.
	 * 
	 * @param plugin The plugin to use to run the thread
	 * @param c The creature instance to move
	 * @param loc The location to start targetting
	 */
	public MobTargettingThread(Plugin plugin, Creature c, Player p) {
		this.plugin = plugin;
		this.c = c;
		setTarget(p);
	}

	/**
	 * Gets the speed of the creature moving.
	 * 
	 * @return The creature speed
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Changes the target of the mob.
	 * 
	 * @param l The new target
	 */
	public void setTarget(Player p) {
		cancel();
		if (p != null) {
			this.p = p;
			target();
		}
	}

	/**
	 * Sets the speed of movement of the mob.
	 * 
	 * @param speed How fast the mob should move
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/*
	 * Begins the targetting thread.
	 */
	private void target() {
		hasTarget = true;
		id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
			public void run() {
				if (!c.isDead() && !p.isDead()) {
					moveMob();
				} else {
					cancel();
				}
			}
		}, 1, 1);
	}

	/**
	 * Cancels the thread.
	 */
	protected void cancel() {
		hasTarget = false;
		plugin.getServer().getScheduler().cancelTask(id);
	}

	/*
	 * Moves the mob towards the target.
	 */
	private void moveMob() {
		Location loc = p.getLocation();
		EntityCreature mob = (EntityCreature) ((CraftCreature) c).getHandle();
		PathEntity path = mob.world.a(mob, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), radius, true, false, false, true);
		mob.setPathEntity(path);
		mob.getNavigation().a(path, speed);
	}

	/**
	 * Checks if the mob has a target.
	 * 
	 * @return Whether or not the mob has a target
	 */
	public boolean hasTarget() {
		return hasTarget;
	}
}
