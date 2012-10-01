package com.github.JamesNorris;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.server.EntityCreature;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Util.Breakable;

public class MobTargetter {
	@SuppressWarnings("unused") private ZAMob ge;
	private int id;

	/**
	 * An instance used in targetting players, that also solves mob speed improvement.
	 * 
	 * @param e The entity that is doing the targetting
	 * @param p The player to target
	 * @param autorun Whether or not to autorun the thread attached to this instance, that targets the player
	 */
	public MobTargetter(ZAMob ge) {
		this.ge = ge;
	}

	/**
	 * Cancels the thread.
	 */
	protected void cancel() {
		Bukkit.getScheduler().cancelTask(id);
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
	 * Starts a per-tick thread that targets the player.
	 * 
	 * @param e The entity that is doing the targetting
	 * @param p The player to target
	 * @param speed The speed to set the entity to
	 */
	public void target(final Entity e, final Player p, final double speed) {
		final EntityCreature ec = (EntityCreature) Breakable.getNMSEntity(e);
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
			public void run() {
				if (!(e.isDead() || p.isDead())) {
					Location l = p.getLocation();
					double pX = l.getX();
					double pZ = l.getZ();
					Location loc = e.getLocation();
					double eX = loc.getX();
					double eY = loc.getY();
					double eZ = loc.getZ();
					double movX = eX - speed;
					double movY = eY;// TODO make it so zombies will stop moving if a block is in front of them, and they will jumkp if there is just one at their feet
					double movZ = eZ - speed;
					if ((eX - pX) < 0)
						movX = eX + speed;
					if ((eZ - pZ) < 0)
						movZ = eZ + speed;
					ec.setPosition(movX, movY, movZ);
				} else {
					cancel();
				}
			}
		}, 1, 1);
	}
}
