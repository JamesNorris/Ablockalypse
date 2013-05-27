package com.github.jamesnorris.implementation;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.GameEntityType;
import com.github.jamesnorris.implementation.serialized.SerialUndead;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.inter.Permadatable;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.inter.ZAScheduledTask;
import com.github.jamesnorris.inter.ZAThread;
import com.github.jamesnorris.manager.SpawnManager;
import com.github.jamesnorris.threading.MobTargettingThread;
import com.github.jamesnorris.util.MiscUtil;

public class Undead implements ZAMob, GameObject, Permadatable {
    private int absorption = 0;
    private DataContainer data = Ablockalypse.getData();
    private boolean fireproof, removed;
    private Game game;
    private MobTargettingThread mtt;
    private Zombie zombie;
    private ZAThread thread;

    /**
     * Creates a new instance of the zombie for ZA.
     * 
     * @param zombie The zombie to be made into this instance
     * @param game The game to involve this zombie in
     */
    public Undead(World world, UUID entityUUID, Game game) {
        this.zombie = (Zombie) data.getEntityByUUID(world, entityUUID);
        this.game = game;
        data.gameObjects.add(this);
        data.mobs.add(this);
        game.addObject(this);
        absorption = (int) (game.getLevel() / Math.sqrt(.5 * game.getLevel()));// less than a hellhound
        fireproof = true;
        final Player p = game.getRandomLivingPlayer();
        final Barrier targetbarrier = SpawnManager.getClosestBarrier(game, p.getLocation());
        mtt = new MobTargettingThread(zombie, targetbarrier != null ? targetbarrier.getCenter() : p.getLocation(), 0.05D, true);
        if (targetbarrier == null) {
            final Location previous = p.getLocation();
            thread = Ablockalypse.getMainThread().scheduleRepeatingTask(new ZAScheduledTask() {
                @Override public void run() {
                    if (p.isDead()) {
                        thread.remove();
                    }
                    if (!MiscUtil.locationMatch(previous, p.getLocation())) {
                        mtt.setTarget(p.getLocation());
                    }
                }
            }, 1);
        }
        zombie.setHealth(10);
        game.setMobCount(game.getMobCount() + 1);
        if (!data.undead.contains(this)) {
            data.undead.add(this);
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

    @Override public Block getDefiningBlock() {
        return zombie.getLocation().getBlock();
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
     * Gets the Entity instance of the mob.
     * 
     * @return The Entity associated with this instance
     */
    @Override public org.bukkit.entity.Entity getEntity() {
        return zombie;
    }

    /**
     * Gets the game the zombie is in.
     * 
     * @return The game that this zombie is involved in
     */
    @Override public Game getGame() {
        return game;
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
     * Gets the speed of the entity.
     * 
     * @return The speed of the entity as a double
     */
    @Override public double getSpeed() {
        return mtt.getNodesPerTick();
    }

    /**
     * Gets the target of the mob.
     * 
     * @return The mobs' target as a location
     */
    @Override public Location getTargetLocation() {
        return mtt.getTarget();
    }

    /**
     * Gets the targetter for this mob.
     * 
     * @return The targetter attached to this instance
     */
    @Override public MobTargettingThread getTargetter() {
        return mtt;
    }

    @Override public GameEntityType getType() {
        return GameEntityType.UNDEAD;
    }

    /**
     * Gets the zombie associated with this instance.
     * 
     * @return The zombie attached to this instance
     */
    public Zombie getZombie() {
        return zombie;
    }

    /**
     * Checks whether or not the zombies is fireproof.
     * 
     * @return Whether or not the zombie is fireproof
     */
    public boolean isFireproof() {
        return fireproof;
    }

    /**
     * Kills the undead and finalizes the instance.
     */
    @Override public void kill() {
        if (zombie != null && !removed) {
            if (game != null && game.hasMob(this)) {
                game.removeObject(this);
            }
            zombie.getWorld().playEffect(zombie.getLocation(), Effect.EXTINGUISH, 1);
            zombie.remove();
            removed = true;
        }
        game.setMobCount(game.getMobCount() - 1);
        data.gameObjects.remove(this);
    }

    /**
     * Removes the undead completely.
     */
    @Override public void remove() {
        kill();
    }

    /**
     * Changes the fireproof ability of the zombie.
     * 
     * @param tf Whether or not the zombie should be fireproof
     */
    public void setFireproof(boolean tf) {
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
     * Sets the amount of damage that the mob can absorb each hit, before it hurts the mob.
     * NOTE: If this nulls out the damage, the damage will automatically be set to 1.
     * 
     * @param i The damage absorption of this mob
     */
    @Override public void setHitAbsorption(int i) {
        absorption = i;
    }

    /**
     * Sets the speed of the entity.
     * 
     * @param nodesPerTick The speed to set the entity to
     */
    @Override public void setSpeed(double nodesPerTick) {
        mtt.setNodesPerTick(nodesPerTick);
    }

    /**
     * Sets the target of this instance.
     * 
     * @param loc The location to target
     */
    @Override public void setTargetLocation(Location loc) {
        mtt.setTarget(loc);
    }

    @Override public Permadata getSerializedVersion() {
        return new SerialUndead(this);
    }
}