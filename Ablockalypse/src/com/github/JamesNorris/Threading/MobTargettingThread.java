package com.github.JamesNorris.Threading;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.Interface.ZAMob;

public class MobTargettingThread {
	private ZAMob ge;
	private net.minecraft.server.Entity ec;
	private int id;
	private Entity e;
	private Player p;
	private double speed;

	/**
	 * An instance used in targetting players, that also solves mob speed improvement.
	 * 
	 * @param e The entity that is doing the targetting
	 * @param p The player to target
	 * @param autorun Whether or not to autorun the thread attached to this instance, that targets the player
	 */
	public MobTargettingThread(ZAMob ge) {
		this.ge = ge;
	}

	/**
	 * Cancels the thread.
	 */
	protected void cancel() {
		Bukkit.getScheduler().cancelTask(id);
	}

	/**
	 * Starts a per-tick thread that targets the player.
	 * 
	 * @param entity The entity that is doing the targetting
	 * @param player The player to target
	 * @param speed The speed to set the entity to
	 */
	public void target(Entity entity, Player player, double speedPerTick) {
		this.e = entity;
		this.p = player;
		this.speed = speedPerTick;
		this.ec = ((CraftEntity) e).getHandle();
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
			public void run() {
				if (!(e.isDead() || p.isDead())) {
					Location l = p.getLocation();
					World w = e.getWorld();
					double pX = l.getX();
					double pZ = l.getZ();
					Location loc = e.getLocation();
					double eX = loc.getX();
					double eY = loc.getY();
					double eZ = loc.getZ();
					double movX = eX - speed;
					double movY = eY;
					double movZ = eZ - speed;
					if ((eX - pX) < 0)
						movX = eX + speed;
					if ((eZ - pZ) < 0)
						movZ = eZ + speed;
					if (!w.getBlockAt((int) movX, (int) movY, (int) movZ).isEmpty()) {// TODO this needs to be more precise
						if (w.getBlockAt((int) movX, (int) movY + 1, (int) movZ).isEmpty()) {
							ec.setPosition(movX, ++movY, movZ);
						}
					} else {
						ec.setPosition(movX, movY, movZ);
						((org.bukkit.entity.Creature) ge.getEntity()).setTarget(p);
					}
				} else {
					cancel();
				}
			}
		}, 1, 1);
	}
}
