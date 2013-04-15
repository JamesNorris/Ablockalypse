package com.github.jamesnorris.implementation;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.enumerated.GameEntityType;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.threading.MobReRandomTeleportThread;
import com.github.jamesnorris.threading.MobTargettingThread;

public class Hellhound extends DataManipulator implements ZAMob, GameObject {
    private int absorption = 0;
    private Game game;
    private MobTargettingThread mtt;
    private Wolf wolf;

    /**
     * Creates a new instance of the GameWolf for ZA.
     * 
     * @param wolf The wolf to be made into this instance
     * @param game The game to involve this wolf in
     */
    public Hellhound(Wolf wolf, Game game) {
        this.wolf = wolf;
        this.game = game;
        wolf.getWorld().strikeLightning(wolf.getLocation());
        data.gameObjects.add(this);
        data.mobs.add(this);
        game.getSpawnManager().mobs.add(this);
        absorption = (int) ((.75 / 2) * game.getLevel() + 1);// slightly more than undead, raises .75 every round
        wolf.setHealth(8);
        Player p = game.getRandomLivingPlayer();
        Barrier targetbarrier = game.getSpawnManager().getClosestBarrier(p.getLocation());
        mtt = new MobTargettingThread((Creature) wolf, (targetbarrier != null) ? targetbarrier.getCenter() : p, true);
        game.setMobCount(game.getMobCount() + 1);
        setAggressive(true);
        if (!data.hellhounds.contains(this))
            data.hellhounds.add(this);
        mtt.setNodesPerTick(0.7D);
        if (game.getLevel() >= (Integer) Setting.DOUBLE_SPEED_LEVEL.getSetting())
            mtt.setNodesPerTick(0.9D);
        new MobReRandomTeleportThread(wolf, game, true, 400);
    }

    /**
     * Adds the mobspawner flames effect to the GameWolf for 1 second.
     */
    public void addFlames() {
        ZAEffect.FLAMES.play(wolf.getLocation());
    }

    /**
     * Gets the creature associated with this mob.
     * 
     * @return The creature associated with this mob
     */
    public Creature getCreature() {
        return wolf;
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(wolf.getLocation().subtract(0, 1, 0).getBlock());
        return blocks;
    }

    /**
     * Gets the Entity instance of the mob.
     * 
     * @return The Entity associated with this instance
     */
    public Entity getEntity() {
        return wolf;
    }

    /**
     * Gets the ZAGame that the hellhound is in.
     * 
     * @return The ZAGame this hellhound is in
     */
    @Override public Game getGame() {
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
     * @return The speed of the entity as an integer
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
     * Gets the Wolf instance associated with this instance.
     * 
     * @return The Wolf instance associated with this instance
     */
    public Wolf getWolf() {
        return wolf;
    }

    /**
     * Kills the wolf and finalizes the instance.
     */
    public void kill() {
        if (wolf != null) {
            if (game.getSpawnManager().mobs.contains(this))
                game.getSpawnManager().mobs.remove(this);
            wolf.getWorld().playEffect(wolf.getLocation(), Effect.EXTINGUISH, 1);
            wolf.remove();
        }
        data.gameObjects.remove(this);
        game.setMobCount(game.getMobCount() - 1);
        game = null;
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
    public void setAggressive(boolean tf) {
        wolf.setAngry(tf);
    }

    /**
     * Adds health to the wolf, mostly used in progressive health addition.
     * 
     * @param amt The amount of health to add to the wolf
     */
    public void setHealth(int amt) {
        wolf.setHealth(amt);
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
        return wolf.getLocation().getBlock();
    }

    @Override public MobTargettingThread getTargetter() {
        return mtt;
    }

    @Override public GameEntityType getType() {
        return GameEntityType.HELLHOUND;
    }

    @Override public GameObjectType getObjectType() {
        return GameObjectType.HELLHOUND;
    }
}
