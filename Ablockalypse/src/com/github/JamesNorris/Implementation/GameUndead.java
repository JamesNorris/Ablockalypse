package com.github.JamesNorris.Implementation;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.NBTTagCompound;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.Undead;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Threading.MobTargettingThread;
import com.github.JamesNorris.Util.Breakable;

public class GameUndead extends Entity implements Undead, ZAMob {
	private ZAGame game;
	private int healthupdate;
	public boolean killed, fireproof;
	private MobTargettingThread mt;
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
		this.mt = new MobTargettingThread(this);
		game.addMobCount();
		mt.target((org.bukkit.entity.Entity) zombie, game.getRandomLivingPlayer(), speed);
		this.healthupdate = game.getLevel();
		if (!Data.undead.contains(this))
			Data.undead.add(this);
		if (game.getLevel() >= External.getYamlManager().getConfigurationData().doubleSpeedLevel)
			setSpeed(getSpeed() * 1.5);
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
	@Override public double getSpeed() {
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
	@Override public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Sets the target of this instance.
	 * 
	 * @param p The player to target
	 */
	@Override public void setTarget(Player p) {
		this.target = p;
		Entity e = (Entity) Breakable.getNMSEntity(zombie);
		Entity p2 = (Entity) Breakable.getNMSPlayer(p);
		EntityHuman eh = e.world.findNearbyPlayer(p2, 1000);
		Player p3 = (Player) eh.getBukkitEntity();
		if (p != null)
			mt.target((org.bukkit.entity.Entity) zombie, p3, speed);
	}

	/**
	 * Gets the Entity instance of the mob.
	 * 
	 * @return The Entity associated with this instance
	 */
	@Override public org.bukkit.entity.Entity getEntity() {
		return (org.bukkit.entity.Entity) zombie;
	}
}
