package com.github.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.behavior.GameObject;
import com.github.behavior.ZAMob;
import com.github.behavior.ZAScheduledTask;
import com.github.behavior.ZAThread;
import com.github.enumerated.GameEntityType;
import com.github.manager.SpawnManager;
import com.github.threading.inherent.MobReRandomTeleportThread;
import com.github.threading.inherent.MobTargettingThread;
import com.github.utility.MiscUtil;
import com.github.utility.serial.SavedVersion;

public class Hellhound extends PermanentAspect implements ZAMob, GameObject {
    private double absorption = 0;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private MobTargettingThread mtt;
    private Wolf wolf;
    private ZAThread thread;

    public Hellhound(SavedVersion savings) {
        this(Bukkit.getWorld((UUID) savings.get("world_uuid")), (UUID) savings.get("wolf_uuid"), Ablockalypse.getData().getGame((String) savings.get("game_name"), true));
        absorption = (Double) savings.get("hit_absorption");
    }

    /**
     * Creates a new instance of the GameWolf for ZA.
     * 
     * @param wolf The wolf to be made into this instance
     * @param game The game to involve this wolf in
     */
    public Hellhound(World world, UUID entityUUID, Game game) {
        wolf = (Wolf) data.getEntityByUUID(world, entityUUID);
        this.game = game;
        absorption = game.getLevel() / Math.sqrt(.35 * game.getLevel());// more than an undead
        if (wolf == null) {
            game.setMobCountSpawnedInThisRound(game.getMobCountSpawnedInThisRound() - 1);
            remove();
        } else {
            data.objects.add(this);
            game.addObject(this);
            wolf.getWorld().strikeLightning(wolf.getLocation());
            wolf.setHealth(8.0D);
            final Player p = game.getRandomLivingPlayer();
            final Barrier targetbarrier = SpawnManager.getClosestBarrier(game, p.getLocation());
            mtt = new MobTargettingThread(wolf, targetbarrier != null ? targetbarrier.getCenter() : p.getLocation(), 0.1D, true);
            if (targetbarrier == null) {
                final Location previous = p.getLocation();
                thread = Ablockalypse.getMainThread().scheduleRepeatingTask(new ZAScheduledTask() {
                    @Override public void run() {
                        if (p.isDead()) {
                            thread.remove();
                            return;
                        }
                        if (!MiscUtil.locationMatch(previous, p.getLocation())) {
                            mtt.setTarget(p.getLocation());
                        }
                    }
                }, 1);
            }
            game.setMobCount(game.getMobCount() + 1);
            setAggressive(true);
            new MobReRandomTeleportThread(wolf, game, true, 400);
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

    @Override public Block getDefiningBlock() {
        if (wolf == null) {
            return null;
        }
        return wolf.getLocation().clone().subtract(0, 1, 0).getBlock();
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(getDefiningBlock());
        return blocks;
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
    @Override public Game getGame() {
        return game;
    }

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID() + ">";
    }

    /**
     * Gets the hit damage that can be absorbed by this mob.
     * 
     * @return The amount of damage to be absorbed each time this mob is hit
     */
    @Override public double getHitAbsorption() {
        return absorption;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("hit_absorption", absorption);
        savings.put("game_name", game.getName());
        savings.put("wolf_uuid", wolf.getUniqueId());
        savings.put("world_uuid", wolf.getWorld().getUID());
        return new SavedVersion(getHeader(), savings, getClass());
    }

    /**
     * Gets the speed of the entity.
     * 
     * @return The speed of the entity as an integer
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

    @Override public MobTargettingThread getTargetter() {
        return mtt;
    }

    @Override public GameEntityType getType() {
        return GameEntityType.HELLHOUND;
    }

    @Override public UUID getUUID() {
        return wolf.getUniqueId();
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
    @Override public void kill() {
        if (game != null && game.hasMob(this)) {
            game.removeObject(this);
        }
        if (wolf != null) {
            wolf.getWorld().playEffect(wolf.getLocation(), Effect.EXTINGUISH, 1);
            wolf.remove();
            wolf = null;
        }
        data.objects.remove(this);
        game.setMobCount(game.getMobCount() - 1);
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
    @Override public void setHealth(double amt) {
        wolf.setHealth(amt);
    }

    /**
     * Sets the amount of damage that the mob can absorb each hit, before it hurts the mob.
     * NOTE: If this nulls out the damage, the damage will automatically be set to 1.
     * 
     * @param i The damage absorption of this mob
     */
    @Override public void setHitAbsorption(double i) {
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

    @Override public void setTargetLocation(Location loc) {
        mtt.setTarget(loc);
    }
}
