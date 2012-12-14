package com.github.JamesNorris.Implementation;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Interface.Barrier;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.HellHound;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Threading.MobTargettingThread;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;

public class GameHellHound extends DataManipulator implements HellHound, GameObject {
	private ZAGame game;
	private MobTargettingThread mt;
	private Object target;
	private Wolf wolf;
	private int id;
	private boolean subtracted = false;
	private int absorption = 0;

	/**
	 * Creates a new instance of the GameWolf for ZA.
	 * 
	 * @param wolf The wolf to be made into this instance
	 */
	public GameHellHound(Wolf wolf, ZAGame game) {
		EffectUtil.generateEffect(wolf.getLocation().getWorld(), wolf.getLocation(), ZAEffect.LIGHTNING);
		data.objects.add(this);
		data.mobs.add(this);
		this.wolf = wolf;
		this.game = game;
		absorption = (int) ((.75 / 2) * game.getLevel() + 1);// slightly more than undead, raises .75 every round
		wolf.setHealth(8);
		Player p = game.getRandomLivingPlayer();
		Barrier targetbarrier = game.getSpawnManager().getClosestBarrier(p.getLocation());
		if (targetbarrier != null) {
			Location gbloc = targetbarrier.getCenter();
			mt = new MobTargettingThread(Ablockalypse.instance, wolf, gbloc);
		} else
			mt = new MobTargettingThread(Ablockalypse.instance, wolf, p);
		game.setMobCount(game.getMobCount() + 1);
		setAggressive(true);
		if (!data.hellhounds.contains(this))
			data.hellhounds.add(this);
		setSpeed(0.28F);
		if (game.getLevel() >= External.getYamlManager().getConfigurationData().doubleSpeedLevel)
			setSpeed(0.32F);
		final Wolf finalwolf = wolf;
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
			@Override public void run() {
				if (!getCreature().isDead() && data.isZAMob(getEntity())) {
					Location target = getGame().getRandomLivingPlayer().getLocation();
					Location strike = getGame().getSpawnManager().findSpawnLocation(target, 5, 3);
					finalwolf.teleport(strike);
					EffectUtil.generateEffect(strike.getWorld(), strike, ZAEffect.LIGHTNING);
				} else
					cancel();
			}
		}, 200, 200);
	}

	/**
	 * Sets the amount of damage that the mob can absorb each hit, before it hurts the mob.
	 * NOTE: If this nulls out the damage, the damage will automatically be set to 1.
	 * 
	 * @param i The damage absorption of this mob
	 */
	@Override public void setHitAbsorption(int i) {
		absorption = i;
	}

	/**
	 * Gets the hit damage that can be absorbed by this mob.
	 * 
	 * @return The amount of damage to be absorbed each time this mob is hit
	 */
	@Override public int getHitAbsorption() {
		return absorption;
	}

	/**
	 * Gets the blocks that defines this object as an object.
	 * 
	 * @return The blocks assigned to this object
	 */
	@Override public ArrayList<Block> getDefiningBlocks() {
		return null;
	}

	/**
	 * Adds the mobspawner flames effect to the GameWolf for 1 second.
	 */
	@Override public void addFlames() {
		EffectUtil.generateEffect(game.getRandomLivingPlayer(), wolf.getLocation(), ZAEffect.FLAMES);
	}

	/*
	 * Cancels the position changing task started when the wolf is instantiated.
	 */
	private void cancel() {
		Bukkit.getScheduler().cancelTask(id);
	}

	/**
	 * Clears all data from this instance.
	 */
	@Override public void finalize() {
		if (!subtracted) {
			game.setMobCount(game.getMobCount() - 1);
			subtracted = true;
		}
	}

	/**
	 * Gets the creature associated with this mob.
	 * 
	 * @return The creature associated with this mob
	 */
	@Override public Creature getCreature() {
		return wolf;
	}

	/**
	 * Gets the Entity instance of the mob.
	 * 
	 * @return The Entity associated with this instance
	 */
	@Override public Entity getEntity() {
		return wolf;
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
	 * @return The mobs' target as a location
	 */
	@Override public Location getTargetLocation() {
		return (Location) target;
	}

	/**
	 * Gets the target of the mob.
	 * 
	 * @return The mobs' target
	 */
	@Override public Player getTargetPlayer() {
		return (Player) target;
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
	 * Kills the wolf and finalizes the instance.
	 */
	@Override public void kill() {
		if (wolf != null) {
			if (game.getSpawnManager().mobs.contains(this))
				game.getSpawnManager().mobs.remove(this);
			wolf.getWorld().playEffect(wolf.getLocation(), Effect.EXTINGUISH, 1);
			wolf.remove();
		}
		data.objects.remove(this);
		finalize();
	}

	/**
	 * Removes the hellhound completely.
	 */
	@Override public void remove() {
		kill();
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
	 * Sets the target of this instance.
	 * 
	 * @param loc The location to target
	 */
	@Override public void setTargetLocation(Location loc) {
		target = loc;
		mt.setTarget(loc);
	}

	/**
	 * Sets the wolfs' target.
	 * 
	 * @param player The player to be made into the target
	 */
	@Override public void setTargetPlayer(Player player) {
		target = player;
		mt.setTarget(player);
	}
}
