package com.github.Ablockalypse.JamesNorris.Implementation;

import java.lang.reflect.Field;

import net.minecraft.server.EntityWolf;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Interface.HellHound;

public class GameHellHound implements HellHound {
	private int health;
	private Wolf wolf;
	private World world;

	/**
	 * Creates a new instance of the GameWolf for ZA.
	 * 
	 * @param wolf The wolf to be made into this instance
	 */
	public GameHellHound(Wolf wolf) {
		this.wolf = wolf;
		world = wolf.getWorld();
		setAggressive(true);
		if (!Data.wolves.contains(this))
			Data.wolves.add(this);
	}

	/**
	 * Adds the mobspawner flames effect to the GameWolf for 1 second.
	 */
	@Override public void addEffect() {
		world.playEffect(wolf.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
	}

	/**
	 * Adds health to the wolf, mostly used in progressive health addition.
	 * 
	 * @param amt The amount of health to add to the wolf
	 */
	@Override public void addHealth(int amt) {
		health = wolf.getHealth();
		wolf.setHealth(health + amt);
	}

	/**
	 * Gets the Wolf instance associated with this instance.
	 * 
	 * @return The Wolf instance associated with this instance
	 */
	@Override public Wolf getWolf() {
		return wolf;
	}

	/**
	 * Gets the world the wolf is in.
	 * 
	 * @return The world the wolf is in
	 */
	@Override public World getWorld() {
		world = wolf.getWorld();
		return world;
	}

	/**
	 * Increases the speed of the wolf.
	 */
	@Override public void increaseSpeed() {
		EntityWolf ew = ((CraftWolf) wolf).getHandle();
		Field field;
		try {
			field = net.minecraft.server.EntityWolf.class.getDeclaredField("bw");
			field.setAccessible(true);
			field.set(ew, 0.6);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Changes the GameWolfs' state to angry.
	 * 
	 * @param tf Whether or not to make the wolf aggressive
	 */
	@Override public void setAggressive(boolean tf) {
		wolf.setAngry(tf);
	}

	/**
	 * Sets the GameWolfs' target.
	 * 
	 * @param player The player to be made into the target
	 */
	@Override public void setTarget(Player player) {
		LivingEntity le = player;
		wolf.setTarget(le);
	}
}
