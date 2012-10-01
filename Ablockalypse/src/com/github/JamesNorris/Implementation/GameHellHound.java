package com.github.JamesNorris.Implementation;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.github.JamesNorris.MobTargetter;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.HellHound;
import com.github.JamesNorris.Interface.ZAGame;

public class GameHellHound implements HellHound, ZAMob {
	private ZAGame game;
	public boolean killed;
	private MobTargetter mt;
	private double speed;
	private Player target;
	private Wolf wolf;
	private World world;

	/**
	 * Creates a new instance of the GameWolf for ZA.
	 * 
	 * @param wolf The wolf to be made into this instance
	 */
	public GameHellHound(Wolf wolf, ZAGame game) {
		this.wolf = wolf;
		this.game = game;
		world = wolf.getWorld();
		this.speed = .05;
		this.mt = new MobTargetter(this);
		mt.target((Entity) wolf, game.getRandomLivingPlayer(), speed);
		setAggressive(true);
		if (!Data.hellhounds.contains(this))
			Data.hellhounds.add(this);
	}

	/**
	 * Adds the mobspawner flames effect to the GameWolf for 1 second.
	 */
	@Override public void addEffect() {
		world.playEffect(wolf.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
	}

	/**
	 * Attempts to increase the mob health depending on the level the mob is on.
	 */
	public void attemptHealthIncrease() {}

	/**
	 * Clears all data from this instance.
	 */
	@Override public void finalize() {
		if (!killed) {
			game.subtractMobCount();
		}
	}

	/**
	 * Gets the ZAGame that the hellhound is in.
	 * 
	 * @return The ZAGame this hellhound is in
	 */
	@Override public ZAGame getGame() {
		return game;
	}

	/**
	 * Gets the speed of the entity.
	 * 
	 * @return The speed of the entity as a double
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Gets the target of the mob.
	 * 
	 * @return The mobs' target
	 */
	public Player getTarget() {
		return target;
	}

	/**
	 * Gets the targetter for this mob.
	 * 
	 * @return The targetter attached to this instance
	 */
	public MobTargetter getTargetter() {
		return mt;
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
	 * Kills the wolf and finalizes the instance.
	 */
	public void kill() {
		wolf.getWorld().playEffect(wolf.getLocation(), Effect.EXTINGUISH, 1);
		wolf.remove();
		finalize();
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
	 * Adds health to the wolf, mostly used in progressive health addition.
	 * 
	 * @param amt The amount of health to add to the wolf
	 */
	@Override public void setHealth(int amt) {
		wolf.setHealth(amt);
	}

	/**
	 * Sets the speed of the entity.
	 * Default is .03.
	 * 
	 * @param speed The speed to set the entity to
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Sets the wolfs' target.
	 * 
	 * @param player The player to be made into the target
	 */
	@Override public void setTarget(Player player) {
		this.target = player;
		if (player != null)
			mt.target((Entity) wolf, player, speed);
	}
}
