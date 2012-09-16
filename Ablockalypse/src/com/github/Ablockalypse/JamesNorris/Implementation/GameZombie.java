package com.github.Ablockalypse.JamesNorris.Implementation;

import java.lang.reflect.Field;

import net.minecraft.server.EntityZombie;

import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Interface.ZombieInterface;

public class GameZombie implements ZombieInterface {
	private boolean fire;
	private Zombie zombie;

	/**
	 * Creates a new instance of the GameZombie for ZA.
	 * 
	 * @param zombie The zombie to be made into this instance
	 */
	public GameZombie(Zombie zombie) {
		this.zombie = zombie;
		if (!Data.zombies.contains(this))
			Data.zombies.add(this);
	}

	/**
	 * Gives the zombie health. Mostly used for increasing health through the levels.
	 * 
	 * @param amt The amount of health to add to the zombie
	 */
	@Override public void addHealth(int amt) {
		int current = zombie.getHealth();
		int next = current + amt;
		zombie.setHealth(next);
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
	 */
	@Override public void increaseSpeed() {
		EntityZombie ez = ((CraftZombie) zombie).getHandle();
		Field field;
		try {
			field = net.minecraft.server.EntityZombie.class.getDeclaredField("bw");
			field.setAccessible(true);
			field.set(ez, 0.6);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	 * Sets the zombies' target.
	 * 
	 * @param player The player to become the target of the zombie
	 */
	@Override public void setTarget(Player player) {
		LivingEntity le = (LivingEntity) player;
		zombie.setTarget(le);
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
