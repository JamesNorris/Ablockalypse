package com.github.JamesNorris.Implementation;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.github.Ablockalypse;
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
	private Player target;
	private Wolf wolf;
	private World world;
	private int healthupdate;

	/**
	 * Creates a new instance of the GameWolf for ZA.
	 * 
	 * @param wolf The wolf to be made into this instance
	 */
	public GameHellHound(Wolf wolf, ZAGame game) {
		this.wolf = wolf;
		this.game = game;
		world = wolf.getWorld();
		this.healthupdate = game.getLevel() / 3;
		Player p = game.getRandomLivingPlayer();
		mt = new MobTargettingThread(Ablockalypse.instance, (Creature) wolf, p);
		game.addMobCount();
		setAggressive(true);
		if (!Data.hellhounds.contains(this))
			Data.hellhounds.add(this);
		if (game.getLevel() <= 2)
			wolf.setHealth(game.getLevel() * 5);
		if (game.getLevel() >= External.getYamlManager().getConfigurationData().doubleSpeedLevel)
			setSpeed(0.3F);
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
	@Override public void attemptHealthIncrease() {
		if (healthupdate > 0 && wolf.getHealth() <= 17) {
			--healthupdate;
			wolf.setHealth(20);
		}
	}

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
		return mt.getSpeed();
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
	 * 
	 * @param speed The speed to set the entity to
	 */
	@Override public void setSpeed(float speed) {
		mt.setSpeed(speed);
	}

	/**
	 * Sets the wolfs' target.
	 * 
	 * @param player The player to be made into the target
	 */
	@Override public void setTarget(Player player) {
		target = player;
		mt.setTarget(player);
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
