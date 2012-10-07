package com.github.JamesNorris.Implementation;

import net.minecraft.server.Entity;
import net.minecraft.server.NBTTagCompound;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.github.Ablockalypse;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.Undead;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Threading.MobTargettingThread;
import com.github.JamesNorris.Util.Breakable;

public class GameUndead extends Entity implements Undead, ZAMob {
	private ZAGame game;
	private int healthupdate;
	public boolean killed, fireproof;
	private MobTargettingThread mt;
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
		Player p = game.getRandomLivingPlayer();
		mt = new MobTargettingThread(Ablockalypse.instance, (Creature) zombie, p);
		game.addMobCount();
		healthupdate = game.getLevel() / 3;
		if (!Data.undead.contains(this))
			Data.undead.add(this);
		if (game.getLevel() <= 2)
			zombie.setHealth(game.getLevel() * 5);
		if (game.getLevel() >= External.getYamlManager().getConfigurationData().doubleSpeedLevel)
			setSpeed(0.3F);
	}

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
		if (healthupdate > 0 && zombie.getHealth() <= 17) {
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
		if (!killed)
			game.subtractMobCount();
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
	@Override public double getSpeed() {
		return mt.getSpeed();
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
	@Override public MobTargettingThread getTargetter() {
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
	@Override public void kill() {
		if (zombie != null) {
			zombie.getWorld().playEffect(zombie.getLocation(), Effect.EXTINGUISH, 1);
			zombie.remove();
		}
		finalize();
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
	 * This only updates the next time the target is set.
	 * 
	 * @param speed The speed to set the entity to
	 */
	@Override public void setSpeed(float speed) {
		mt.setSpeed(speed);
	}

	/**
	 * Sets the target of this instance.
	 * 
	 * @param p The player to target
	 */
	@Override public void setTarget(Player p) {
		target = p;
		mt.setTarget(p);
	}

	/**
	 * Gets the Entity instance of the mob.
	 * 
	 * @return The Entity associated with this instance
	 */
	@Override public org.bukkit.entity.Entity getEntity() {
		return zombie;
	}
}
