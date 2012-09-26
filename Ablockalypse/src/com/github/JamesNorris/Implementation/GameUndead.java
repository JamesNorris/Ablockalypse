package com.github.JamesNorris.Implementation;

import java.lang.reflect.Field;

import net.minecraft.server.EntityZombie;

import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.github.Ablockalypse;
import com.github.JamesNorris.PluginMaster;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.Undead;

public class GameUndead implements Undead {
	private boolean fire;
	private PluginMaster pm;
	private Player target;
	private Zombie zombie;

	/**
	 * Creates a new instance of the GameZombie for ZA.
	 * 
	 * @param zombie The zombie to be made into this instance
	 */
	public GameUndead(Zombie zombie) {
		this.zombie = zombie;
		this.pm = Ablockalypse.getMaster();
		toggleFireImmunity();
		if (!Data.zombies.contains(this))
			Data.zombies.add(this);
	}

	/**
	 * Gets the target of the zombie.
	 * 
	 * @return The zombies' target
	 */
	@Override public Player getTarget() {
		return target;
	}

	/**
	 * Gets the world this zombie is located in.
	 * 
	 * @return The world the zombie is located in
	 */
	@Override public World getWorld() {
		return zombie.getWorld();
	}

	/**
	 * Gets the zombie associated with this instance.
	 */
	@Override public Zombie getZombie() {
		return zombie;
	}

	/**
	 * Increases the speed of the zombie.
	 * 
	 * @category breakable This is subject to break
	 */
	@Override public void increaseSpeed() {
		/* BREAKABLE */
		EntityZombie ez = ((CraftZombie) zombie).getHandle();
		Field field;
		try {
			field = net.minecraft.server.EntityZombie.class.getDeclaredField("bw");
			field.setAccessible(true);
			field.set(ez, 0.6);
		} catch (Exception e) {
			pm.crash(pm.getInstance(), e.getCause().toString(), false);
		}
		/* BREAKABLE */
	}

	/**
	 * Checks if the zombie in this instance is on fire or not.
	 * 
	 * @return Whether or not the zombie is on fire
	 */
	@Override public boolean isOnFire() {
		return fire;
	}

	/**
	 * Sets the zombie health. Mostly used for increasing health through the levels.
	 * 
	 * @param amt The amount of health to give to the zombie
	 */
	@Override public void setHealth(int amt) {
		zombie.setHealth(amt);
	}

	/**
	 * Toggles whether or not the zombie should be immune to fire.
	 */
	@Override public void toggleFireImmunity() {
		if (fire) {
			zombie.setFireTicks(0);
		} else {
			zombie.setFireTicks((zombie.getHealth() * 2));
		}
	}
}
