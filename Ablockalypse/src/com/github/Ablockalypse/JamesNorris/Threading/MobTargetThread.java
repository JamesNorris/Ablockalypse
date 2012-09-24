package com.github.Ablockalypse.JamesNorris.Threading;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayerBase;

public class MobTargetThread {
	private int id;
	private Ablockalypse instance;
	private LivingEntity player;
	private ZAPlayerBase zap;
	private Zombie zombie;

	/**
	 * Creates a new instance of the thread.
	 * 
	 * @param zombie The zombie to do the targetting
	 * @param player The player to target
	 * @param autorun Whether or not to autorun the target() method
	 */
	public MobTargetThread(Zombie zombie, LivingEntity player, boolean autorun) {
		this.zombie = zombie;
		this.player = player;
		this.zap = Data.players.get(player);
		instance = Ablockalypse.instance;
		if (autorun)
			target();
	}

	/**
	 * Cancels the thread.
	 */
	protected void cancel() {
		Bukkit.getScheduler().cancelTask(id);
		player = null;
	}

	/*
	 * Removes all data associated with this class.
	 */
	@SuppressWarnings("unused") @Override public void finalize() {
		for (Method m : this.getClass().getDeclaredMethods())
			m = null;
		for (Field f : this.getClass().getDeclaredFields())
			f = null;
	}

	/**
	 * Forces the zombie to target a player constantly.
	 */
	protected void target() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (zombie.getTarget() != player) {
					if (!zombie.isDead() && !player.isDead() && !zap.isInLastStand())
						zombie.setTarget(player);
					else
						cancel();
				}
			}
		}, 40, 40);
	}
}
