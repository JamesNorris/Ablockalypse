package com.github.JamesNorris.Implementation;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.HellHound;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Threading.MobTargettingThread;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.EffectUtil.ZAEffect;

public class GameHellHound implements HellHound, ZAMob {
	private ZAGame game;
	public boolean killed;
	private MobTargettingThread mt;
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
		speed = .05;
		mt = new MobTargettingThread();
		game.addMobCount();
		mt.target(wolf, game.getRandomLivingPlayer(), speed);
		setAggressive(true);
		if (!Data.hellhounds.contains(this))
			Data.hellhounds.add(this);
		if (game.getLevel() >= External.getYamlManager().getConfigurationData().doubleSpeedLevel)
			setSpeed(getSpeed() * 1.5);
	}

	/**
	 * Adds the mobspawner flames effect to the GameWolf for 1 second.
	 */
	@Override public void addEffect() {
		EffectUtil.generateEffect(game.getRandomLivingPlayer(), wolf.getLocation(), ZAEffect.FLAMES);
	}

	/**
	 * Attempts to increase the mob health depending on the level the mob is on.
	 */
	@Override public void attemptHealthIncrease() {}

	/**
	 * Clears all data from this instance.
	 */
	@Override public void finalize() {
		if (!killed)
			game.subtractMobCount();
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
	@Override public double getSpeed() {
		return speed;
	}

	/**
	 * Gets the target of the mob.
	 * 
	 * @return The mobs' target
	 */
	@Override public Player getTarget() {
		return target;
	}

	/**
	 * Gets the targetter for this mob.
	 * 
	 * @return The targetter attached to this instance
	 */
	@Override public MobTargettingThread getTargetter() {
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
	@Override public void kill() {
		if (wolf != null) {
			wolf.getWorld().playEffect(wolf.getLocation(), Effect.EXTINGUISH, 1);
			wolf.remove();
		}
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
	@Override public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Sets the wolfs' target.
	 * 
	 * @param player The player to be made into the target
	 */
	@Override public void setTarget(Player player) {
		target = player;
		if (player != null)
			mt.target(wolf, player, speed);
	}

	/**
	 * Gets the Entity instance of the mob.
	 * 
	 * @return The Entity associated with this instance
	 */
	@Override public Entity getEntity() {
		return wolf;
	}
}
