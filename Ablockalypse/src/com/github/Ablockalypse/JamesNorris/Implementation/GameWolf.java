package com.github.Ablockalypse.JamesNorris.Implementation;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.util.Vector;

import com.github.Ablockalypse.JamesNorris.Data;
import com.github.Ablockalypse.JamesNorris.Interface.WolfInterface;

public class GameWolf implements WolfInterface {
	private int health;
	private Wolf wolf;
	private World world;

	/**
	 * Creates a new instance of the GameWolf for ZA.
	 * 
	 * @param wolf The wolf to be made into this instance
	 */
	public GameWolf(Wolf wolf) {
		this.wolf = wolf;
		this.world = wolf.getWorld();
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
		Vector v = wolf.getVelocity();
		wolf.setVelocity(v.multiply(2));
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
		LivingEntity le = (LivingEntity) player;
		wolf.setTarget(le);
	}
}
