package com.github.JamesNorris.Implementation;

import net.minecraft.server.Entity;
import net.minecraft.server.NBTTagCompound;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.github.JamesNorris.MobTargetter;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.Undead;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Util.Breakable;

public class GameUndead extends Entity implements Undead, ZAMob {
	private ZAGame game;
	private int healthupdate;
	public boolean killed;
	private MobTargetter mt;
	private double speed;
	private Player target;
	private Zombie zombie;

	/**
	 * Creates a new instance of the GameZombie for ZA.
	 * 
	 * @param zombie The zombie to be made into this instance
	 */
	public GameUndead(Zombie zombie, ZAGame game) {
		super(Breakable.getNMSWorld(zombie.getWorld()));
		this.zombie = zombie;
		this.game = game;
		this.speed = .04;
		this.mt = new MobTargetter(this);
		mt.target((org.bukkit.entity.Entity) zombie, game.getRandomLivingPlayer(), speed);
		this.healthupdate = game.getLevel();
		if (this.fireProof == false)
			setFireProof(true);
		if (!Data.undead.contains(this))
			Data.undead.add(this);
	}

	/* BREAKABLE CODE BELOW, THIS MAY NEED TO BE UPDATED */
	/**
	 * NOTE: DO NOT USE
	 */
	@Override protected void a() {}

	/**
	 * NOTE: DO NOT USE
	 */
	@Override protected void a(NBTTagCompound arg0) {}

	/**
	 * Attempts to increase the mob health depending on the level the zombie is on.
	 */
	@Override public void attemptHealthIncrease() {
		if (healthupdate > 0 && zombie.getHealth() <= 15) {
			--healthupdate;
			zombie.setHealth(20);
		}
	}

	/**
	 * NOTE: DO NOT USE
	 */
	@Override protected void b(NBTTagCompound arg0) {}

	/**
	 * Clears all data from this instance.
	 */
	@Override public void finalize() {
		if (!killed) {
			game.subtractMobCount();
		}
	}

	/**
	 * Gets the game the zombie is in.
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
	 * Gets the target of the zombie.
	 * 
	 * @return The zombies' target
	 */
	@Override public Player getTarget() {
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
	 * Kills the undead and finalizes the instance.
	 */
	public void kill() {
		zombie.getWorld().playEffect(zombie.getLocation(), Effect.EXTINGUISH, 1);
		zombie.remove();
		finalize();
	}

	/**
	 * Toggles whether or not the zombie should be immune to fire.
	 */
	@Override public void setFireProof(boolean tf) {
		if (tf) {
			this.fireProof = true;
			zombie.setFireTicks(0);
		} else {
			zombie.setFireTicks((zombie.getHealth() * 2));
			this.fireProof = false;
		}
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
	 * Sets the speed of the entity.
	 * Default is .03.
	 * This only updates the next time the target is set.
	 * 
	 * @param speed The speed to set the entity to
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Sets the target of this instance.
	 * 
	 * @param p The player to target
	 */
	public void setTarget(Player p) {
		this.target = p;
		if (p != null)
			mt.target((org.bukkit.entity.Entity) zombie, p, speed);
	}
}
