package com.github.jamesnorris.implementation;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.GameEntityType;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.threading.MobTargettingThread;

public class Undead implements ZAMob, GameObject {
    private DataContainer data = DataContainer.data;
    private int absorption = 0;
    private boolean fireproof;
    private Game game;
    private MobTargettingThread mtt;
    private Zombie zombie;

    /**
     * Creates a new instance of the GameZombie for ZA.
     * 
     * @param zombie The zombie to be made into this instance
     * @param game The game to involve this zombie in
     */
    public Undead(Zombie zombie, Game game) {
        this.zombie = zombie;
        this.game = game;
        data.gameObjects.add(this);
        data.mobs.add(this);
        game.getSpawnManager().mobs.add(this);
        absorption = (int) ((.5 / 2) * game.getLevel() + 1);// slightly less than wolf, increases at .5 every round
        fireproof = true;
        Player p = game.getRandomLivingPlayer();
        Barrier targetbarrier = game.getSpawnManager().getClosestBarrier(p.getLocation());
        mtt = new MobTargettingThread((Creature) zombie, (targetbarrier != null) ? targetbarrier.getCenter() : p, true);
        zombie.setHealth(10);
        game.setMobCount(game.getMobCount() + 1);
        if (!data.undead.contains(this))
            data.undead.add(this);
        if (game.getLevel() >= (Integer) Setting.DOUBLE_SPEED_LEVEL.getSetting())
            mtt.setNodesPerTick(0.6F);
    }

    /**
     * Gets the creature associated with this mob.
     * 
     * @return The creature associated with this mob
     */
    public Creature getCreature() {
        return zombie;
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
    public org.bukkit.entity.Entity getEntity() {
        return zombie;
    }

    /**
     * Gets the game the zombie is in.
     * 
     * @return The game that this zombie is involved in
     */
    public Game getGame() {
        return game;
    }

    /**
     * Gets the hit damage that can be absorbed by this mob.
     * 
     * @return The amount of damage to be absorbed each time this mob is hit
     */
    public int getHitAbsorption() {
        return absorption;
    }

    /**
     * Gets the speed of the entity.
     * 
     * @return The speed of the entity as a double
     */
    public double getSpeed() {
        return mtt.getNodesPerTick();
    }

    /**
     * Gets the target of the mob.
     * 
     * @return The mobs' target as a location
     */
    public Location getTargetLocation() {
        return mtt.getTargetLocation();
    }

    /**
     * Gets the targetter for this mob.
     * 
     * @return The targetter attached to this instance
     */
    public MobTargettingThread getTargetter() {
        return mtt;
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
    public void kill() {
        if (zombie != null) {
            if (game.getSpawnManager().mobs.contains(this))
                game.getSpawnManager().mobs.remove(this);
            zombie.getWorld().playEffect(zombie.getLocation(), Effect.EXTINGUISH, 1);
            zombie.remove();
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
    public void setHealth(int amt) {
        zombie.setHealth(amt);
    }

    /**
     * Sets the amount of damage that the mob can absorb each hit, before it hurts the mob.
     * NOTE: If this nulls out the damage, the damage will automatically be set to 1.
     * 
     * @param i The damage absorption of this mob
     */
    public void setHitAbsorption(int i) {
        absorption = i;
    }

    /**
     * Sets the speed of the entity.
     * 
     * @param nodesPerTick The speed to set the entity to
     */
    public void setSpeed(double nodesPerTick) {
        mtt.setNodesPerTick(nodesPerTick);
    }

    /**
     * Sets the target of this instance.
     * 
     * @param loc The location to target
     */
    public void setTargetLocation(Location loc) {
        mtt.setTarget(loc);
    }

    @Override public Block getDefiningBlock() {
        return zombie.getLocation().getBlock();
    }

    @Override public GameEntityType getType() {
        return GameEntityType.UNDEAD;
    }

    @Override public GameObjectType getObjectType() {
        return GameObjectType.UNDEAD;
    }
}
