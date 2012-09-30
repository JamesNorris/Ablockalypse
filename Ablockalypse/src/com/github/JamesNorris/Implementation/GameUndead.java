package com.github.JamesNorris.Implementation;

import net.minecraft.server.Entity;
import net.minecraft.server.NBTTagCompound;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.Undead;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Util.Breakable;

public class GameUndead extends Entity implements Undead {
	private Player target;
	private Zombie zombie;
	private ZAGame game;
	private int healthupdate;
	public boolean killed;

	/**
	 * Creates a new instance of the GameZombie for ZA.
	 * 
	 * @param zombie The zombie to be made into this instance
	 */
	public GameUndead(Zombie zombie, ZAGame game) {
		super(Breakable.getNMSWorld(zombie.getWorld()));
		this.zombie = zombie;
		this.game = game;
		this.healthupdate = game.getLevel();
		if (this.fireProof == false)
			setFireProof(true);
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
		// TODO this doesn't work. Find a way to improve zombie speed.
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
	 * Gets the game the zombie is in.
	 */
	@Override public ZAGame getGame() {
		return game;
	}

	/**
	 * Clears all data from this instance.
	 */
	@Override public void finalize() {
		if (!killed) {
			game.subtractMobCount();
		}
	}

	/**
	 * Attempts to increase the mob health depending on the level the zombie is on.
	 */
	@Override public void attemptHealthIncrease() {
		if (healthupdate > 0 && zombie.getHealth() <= 15) {
			--healthupdate;
			zombie.setHealth(20);
		}
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
	 * NOTE: DO NOT USE
	 */
	@Override protected void b(NBTTagCompound arg0) {}
}
