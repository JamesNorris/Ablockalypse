package com.github.JamesNorris.Threading;

/* Breakable Packages */
import net.minecraft.server.v1_4_6.*;
import org.bukkit.craftbukkit.v1_4_6.entity.*;
/* End Breakable Packages */

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Util.MathAssist;

public class MobTargettingThread extends DataManipulator {
	private final Plugin plugin;
	private Player p;
	private Creature c;
	private Location location;
	private int id, wait = 0;
	private boolean hasTarget = false;
	private float speed = 0.18F;// The default speed of the mob
	private float radius = 16.0F;// the radius of the path (if set above 16, will cause weird behavior)
	private int standStill = (int) MathAssist.line(28, 0.18F, 5);// TODO test - time in ticks before the mob should rethink the path (if it is standing still)
	private ZAMob zam;
	private Location last;

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
		if (data.isZAMob(c))
			zam = (ZAMob) (c);
		else
			zam = null;
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
		if (data.isZAMob(c))
			zam = (ZAMob) (c);
		else
			zam = null;
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

	/*
	 * Finds a local checkpoint towards the target.
	 */
	private void checkpoint(Location loc) {// TODO test
		Location l = c.getLocation();
		int X = l.getBlockX();
		int Z = l.getBlockZ();
		double modX = 0;
		double modZ = 0;
		if (X < loc.getBlockX())
			modX = speed;
		else
			modX = -speed;
		if (Z < loc.getBlockZ())
			modZ = speed;
		else
			modZ = -speed;
		EntityCreature mob = ((CraftCreature) c).getHandle();
		mob.setPosition(l.getX() + modX, l.getY(), l.getZ() + modZ);
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
	 * Moves the mob towards the target.
	 */
	private void moveMob(Location loc) {
		if (p != null || location != null) {
			EntityCreature mob = ((CraftCreature) c).getHandle();
			PathEntity path = mob.world.a(mob, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), radius, true, false, false, true);
			mob.setPathEntity(path);
			mob.getNavigation().a(path, speed);
		}
		if (data.barriers.containsKey(location) && location.getBlock().isEmpty() && data.isZAMob(c)) {
			ZAMob zam = data.getZAMob(c);
			if (zam.getGame().getRandomLivingPlayer() != null)
				zam.setTargetPlayer(zam.getGame().getRandomLivingPlayer());
		}
		if (p != null && !data.players.containsKey(p))
			cancel();
	}

	/*
	 * Refreshes the target of the mob.
	 */
	private Location refreshTarget() {
		Location loc = null;
		if (p != null && p.isDead() && data.playerExists(p)) {
			ZAPlayer zap = data.getZAPlayer(p);
			p = zap.getGame().getRandomLivingPlayer();
		}
		if (p != null)
			loc = p.getLocation();
		else if (location != null)
			loc = location;
		return loc;
	}

	/*
	 * Checks if the distance to target is too great to go directly there.
	 */
	private boolean requiresCheckpoint(Location loc, Location target) {
		int highX = loc.getBlockX(), highY = loc.getBlockY(), highZ = loc.getBlockZ();
		int lowX = target.getBlockX(), lowY = target.getBlockY(), lowZ = target.getBlockZ();
		if (loc.getBlockX() < target.getBlockX()) {
			lowX = loc.getBlockX();
			highX = target.getBlockX();
		}
		if (loc.getBlockY() < target.getBlockY()) {
			lowY = loc.getBlockY();
			highY = target.getBlockY();
		}
		if (loc.getBlockZ() < target.getBlockZ()) {
			lowZ = loc.getBlockZ();
			highZ = target.getBlockZ();
		}
		int Xdif = highX - lowX;
		int Ydif = highY - lowY;
		int Zdif = highZ - lowZ;
		return (Xdif > 15 || Ydif > 15 || Zdif > 15);
	}

	/**
	 * Sets the speed of movement of the mob.
	 * 
	 * @param speed How fast the mob should move
	 */
	public void setSpeed(float speed) {
		this.standStill = (int) MathAssist.line(28, speed, 5);
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
	 * Checks if all pathpoints to the player are clear.
	 */
	private boolean pathIsClear(PathEntity pe) {// TODO test
		for (int i = 0; i < 16; i++) {
			PathPoint pt = pe.a(i);
			if (pt != null && !c.getWorld().getBlockAt(pt.a, pt.b, pt.c).isEmpty())
				return false;
		}
		return true;
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
					last = c.getLocation();
					if (requiresCheckpoint(last, target))
						checkpoint(target);
					else
						moveMob(target);
					PathEntity pe = ((CraftCreature) c).getHandle().pathEntity;
					Location newloc = c.getLocation();
					if (zam != null && p != null) {
						if (last.distance(newloc) <= 3 || (pe != null && !pathIsClear(pe)) || target.getWorld() != c.getWorld())
							++wait;
						else
							wait = 0;
						if (wait >= standStill) {// respawn and recalculate the target
							wait = 0;
							ZAGame zag = zam.getGame();
							c.teleport(zag.getSpawnManager().findSpawnLocation(target, 16, 8));
							setTarget(zag.getSpawnManager().getClosestBarrier(p).getCenter());
						}
					}
				} else
					cancel();
			}
		}, 1, 1);
	}
}
