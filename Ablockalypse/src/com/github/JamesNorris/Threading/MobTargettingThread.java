package com.github.JamesNorris.Threading;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.PathEntity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;

public class MobTargettingThread {
	private final Plugin plugin;
	private Player p;
	private Creature c;
	private Location location;
	private int id;
	private boolean hasTarget = false;
	private float speed = 0.18F;
	private float radius = 16.0F;

	/**
	 * Creates a new mobtargetter, that can target specific locations.
	 * 
	 * @param plugin The plugin to use to run the thread
	 * @param c The creature instance to move
	 * @param loc The location to start targetting
	 */
	public MobTargettingThread(Plugin plugin, Creature c, Location loc) {
		this.plugin = plugin;
		this.c = c;
		location = loc;
		setTarget(loc);
	}

	/**
	 * Creates a new mobtargetter, that can target specific locations.
	 * 
	 * @param plugin The plugin to use to run the thread
	 * @param c The creature instance to move
	 * @param p The player to start targetting
	 */
	public MobTargettingThread(Plugin plugin, Creature c, Player p) {
		this.plugin = plugin;
		this.c = c;
		this.p = p;
		setTarget(p);
	}

	/**
	 * Cancels the thread.
	 */
	protected void cancel() {
		hasTarget = false;
		plugin.getServer().getScheduler().cancelTask(id);
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
	 * Checks if the mob has a target.
	 * 
	 * @return Whether or not the mob has a target
	 */
	public boolean hasTarget() {
		return hasTarget;
	}

	/*
	 * Finds a local checkpoint towards the target.
	 */
	private Location checkpoint(Location loc) {// TODO test
		Location l = c.getLocation();
		double X = l.getX();
		double Z = l.getZ();
		double modX = 0;
		double modZ = 0;
		if (X < loc.getX())
			modX = X + 5;
		else
			modX = X - 5;
		if (Z < loc.getZ())
			modZ = Z + 5;
		else
			modZ = Z - 5;
		Location target = l.add(modX, 0, modZ);
		EffectUtil.generateEffect(l.getWorld(), target, ZAEffect.FLAMES);//TODO remove on beta - Meant to test the target of the mob
		return target;
	}

	/*
	 * Refreshes the target of the mob.
	 */
	private Location refreshTarget() {
		Location loc = null;
		if (p != null && p.isDead() && Data.playerExists(p)) {
			ZAPlayer zap = Data.getZAPlayer(p);
			p = zap.getGame().getRandomLivingPlayer();
		}
		if (p != null)
			loc = p.getLocation();
		else if (location != null)
			loc = location;
		return loc;
	}

	/*
	 * Moves the mob towards the target.
	 */
	private void moveMob(Location loc) {
		if (p != null || location != null) {
			EntityCreature mob = ((CraftCreature) c).getHandle();
			PathEntity path = mob.world.a(mob, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), radius, true, false, false, true);
			mob.setPathEntity(path);
			mob.getNavigation().a(path, speed);
		}
		if (Data.barriers.containsKey(location) && location.getBlock().isEmpty() && Data.isZAMob(c)) {
			ZAMob zam = Data.getZAMob(c);
			if (zam.getGame().getRandomLivingPlayer() != null)
				zam.setTargetPlayer(zam.getGame().getRandomLivingPlayer());
		}
		if (p != null && !Data.players.containsKey(p))
			cancel();
	}

	/**
	 * Sets the speed of movement of the mob.
	 * 
	 * @param speed How fast the mob should move
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Changes the target of the mob.
	 * 
	 * @param l The new target
	 */
	public void setTarget(Location l) {
		cancel();
		location = l;
		target();
	}

	/**
	 * Changes the target of the mob.
	 * 
	 * @param l The new target
	 */
	public void setTarget(Player p) {
		cancel();
		if (p != null)
			this.p = p;
		target();
	}

	/*
	 * Begins the targetting thread.
	 */
	private void target() {
		hasTarget = true;
		id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
			@Override public void run() {
				if (!c.isDead() && (p == null || !p.isDead())) {
					Location target = refreshTarget();
					Location loc = c.getLocation();
					double Xdif = Math.abs(loc.getX() - target.getX());
					double Ydif = Math.abs(loc.getY() - target.getY());
					double Zdif = Math.abs(loc.getY() - target.getY());
					if (Xdif > 15 || Ydif > 15 || Zdif > 15)
						target = checkpoint(target);
					moveMob(target);
				} else
					cancel();
			}
		}, 1, 1);
	}
}
