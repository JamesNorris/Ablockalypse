package com.github.JamesNorris.Threading;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;

public class MobTargettingThread {
	private net.minecraft.server.Entity ec;
	private int id;
	private Entity e;
	private Player p;
	private double speed;

	/**
	 * An instance used in targetting players, that also solves mob speed improvement.
	 */
	public MobTargettingThread() {}

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
		e = entity;
		p = player;
		speed = speedPerTick;
		ec = ((CraftEntity) e).getHandle();
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
			@Override public void run() {
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
						if (w.getBlockAt((int) movX, (int) movY + 1, (int) movZ).isEmpty())
							ec.setPosition(movX, ++movY, movZ);
					} else {
						ec.setPosition(movX, movY, movZ);
					}
				} else
					cancel();
			}
		}, 1, 1);
	}
}
