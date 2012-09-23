package com.github.Ablockalypse.JamesNorris.Implementation;

import java.lang.reflect.Field;

import net.minecraft.server.EntityZombie;

import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.PluginMaster;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Interface.ZombieInterface;
import com.github.Ablockalypse.JamesNorris.Threading.MobTargetThread;

public class GameZombie implements ZombieInterface {
	private boolean fire;
	private final Zombie zombie;
	private Player target;
	private PluginMaster pm;

	/**
	 * Creates a new instance of the GameZombie for ZA.
	 * 
	 * @param zombie The zombie to be made into this instance
	 */
	public GameZombie(final Zombie zombie) {
		this.zombie = zombie;
		this.pm = Ablockalypse.getMaster();
		if (!Data.zombies.contains(this))
			Data.zombies.add(this);
	}

	/**
	 * Sets the zombie health. Mostly used for increasing health through the levels.
	 * 
	 * @param amt The amount of health to give to the zombie
	 */
	@Override public void setHealth(final int amt) {
		zombie.setHealth(amt);
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
		/* BREAKABLE */
		final EntityZombie ez = ((CraftZombie) zombie).getHandle();
		Field field;
		try {
			field = net.minecraft.server.EntityZombie.class.getDeclaredField("bw");
			field.setAccessible(true);
			field.set(ez, 0.6);
		} catch (final Exception e) {
			pm.crash(pm.getInstance(), e.getCause().toString(), false);
		}
		/* BREAKABLE */
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
	@Override public void setTarget(final Player player) {
		final LivingEntity le = player;
		target = player;
		new MobTargetThread(zombie, le, true);
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
