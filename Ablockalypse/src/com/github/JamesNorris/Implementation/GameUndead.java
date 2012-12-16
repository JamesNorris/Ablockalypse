package com.github.JamesNorris.Implementation;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Interface.Barrier;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.Undead;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Threading.MobTargettingThread;

public class GameUndead extends DataManipulator implements Undead, GameObject {
	private ZAGame game;
	private MobTargettingThread mt;
	private Object target;
	private Zombie zombie;
	private int absorption = 0;
	private boolean fireproof, subtracted = false;

	/**
	 * Creates a new instance of the GameZombie for ZA.
	 * 
	 * @param zombie The zombie to be made into this instance
	 */
	public GameUndead(Zombie zombie, ZAGame game) {
		data.objects.add(this);
		data.mobs.add(this);
		this.zombie = zombie;
		this.game = game;
		absorption = (int) ((.5 / 2) * game.getLevel() + 1);// slightly less than wolf, increases at .5 every round
		fireproof = true;
		Player p = game.getRandomLivingPlayer();
		Barrier targetbarrier = game.getSpawnManager().getClosestBarrier(p.getLocation());
		if (targetbarrier != null) {
			Location gbloc = targetbarrier.getCenter();
			mt = new MobTargettingThread(Ablockalypse.instance, zombie, gbloc);
		} else
			mt = new MobTargettingThread(Ablockalypse.instance, zombie, p);
		zombie.setHealth(10);
		game.setMobCount(game.getMobCount() + 1);
		if (!data.undead.contains(this))
			data.undead.add(this);
		if (game.getLevel() >= External.getYamlManager().getConfigurationData().doubleSpeedLevel)
			setSpeed(0.24F);
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
		ArrayList<Block> blocks = new ArrayList<Block>();
		blocks.add(zombie.getLocation().subtract(0, 1, 0).getBlock());
		return blocks;
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
		return zombie;
	}

	/**
	 * Gets the Entity instance of the mob.
	 * 
	 * @return The Entity associated with this instance
	 */
	@Override public org.bukkit.entity.Entity getEntity() {
		return zombie;
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
	 * Gets the target of the mob.
	 * 
	 * @return The mobs' target as a location
	 */
	@Override public Location getTargetLocation() {
		return (Location) target;
	}

	/**
	 * Gets the target of the zombie.
	 * 
	 * @return The zombies' target
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
	 * Gets the zombie associated with this instance.
	 */
	@Override public Zombie getZombie() {
		return zombie;
	}

	/**
	 * Checks whether or not the zombies is fireproof.
	 * 
	 * @return Whether or not the zombie is fireproof
	 */
	@Override public boolean isFireproof() {
		return fireproof;
	}

	/**
	 * Kills the undead and finalizes the instance.
	 */
	@Override public void kill() {
		if (zombie != null) {
			if (game.getSpawnManager().mobs.contains(this))
				game.getSpawnManager().mobs.remove(this);
			zombie.getWorld().playEffect(zombie.getLocation(), Effect.EXTINGUISH, 1);
			zombie.remove();
		}
		finalize();
	}

	/**
	 * Removes the undead completely.
	 */
	@Override public void remove() {
		kill();
		data.objects.remove(this);
	}

	/**
	 * Changes the fireproof ability of the zombie.
	 * 
	 * @param tf Whether or not the zombie should be fireproof
	 */
	@Override public void setFireproof(boolean tf) {
		fireproof = tf;
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
	 * @param loc The location to target
	 */
	@Override public void setTargetLocation(Location loc) {
		target = loc;
		mt.setTarget(loc);
	}

	/**
	 * Sets the target of this instance.
	 * 
	 * @param p The player to target
	 */
	@Override public void setTargetPlayer(Player p) {
		target = p;
		mt.setTarget(p);
	}
}
