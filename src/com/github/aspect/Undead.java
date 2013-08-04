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
import org.bukkit.entity.Zombie;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.behavior.GameObject;
import com.github.behavior.ZAMob;
import com.github.behavior.ZAScheduledTask;
import com.github.behavior.ZAThread;
import com.github.enumerated.GameEntityType;
import com.github.manager.SpawnManager;
import com.github.threading.inherent.MobTargettingThread;
import com.github.utility.MiscUtil;
import com.github.utility.serial.SavedVersion;

public class Undead extends PermanentAspect implements ZAMob, GameObject {
    private double absorption = 0;
    private DataContainer data = Ablockalypse.getData();
    private boolean fireproof;
    private Game game;
    private MobTargettingThread mtt;
    private Zombie zombie;
    private ZAThread thread;
    public UUID testUUID = UUID.randomUUID();

    public Undead(SavedVersion savings) {
        this(Bukkit.getWorld((UUID) savings.get("world_uuid")), (UUID) savings.get("zombie_uuid"), Ablockalypse.getData().getGame((String) savings.get("game_name"), true));
        absorption = (Double) savings.get("hit_absorption");
        fireproof = (Boolean) savings.get("is_fireproof");
    }

    /**
     * Creates a new instance of the zombie for ZA.
     * 
     * @param zombie The zombie to be made into this instance
     * @param game The game to involve this zombie in
     */
    public Undead(World world, UUID entityUUID, Game game) {
        zombie = (Zombie) data.getEntityByUUID(world, entityUUID);
        this.game = game;
        absorption = game.getLevel() / Math.sqrt(.5 * game.getLevel());// less than a hellhound
        fireproof = true;
        if (zombie == null) {
            remove();
        } else {
            data.objects.add(this);
            game.addObject(this);
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
            zombie.setHealth(10.0D);
            game.setMobCount(game.getMobCount() + 1);
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
        if (zombie == null) {
            return null;
        }
        return zombie.getLocation().clone().subtract(0, 1, 0).getBlock();
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(getDefiningBlock());// why is zombie null?...
        return blocks;
    }

    /**
     * Gets the Entity instance of the mob.
     * 
     * @return The Entity associated with this instance
     */
    @Override public Entity getEntity() {
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
        savings.put("is_fireproof", fireproof);
        savings.put("game_name", game.getName());
        savings.put("zombie_uuid", zombie.getUniqueId());
        savings.put("world_uuid", zombie.getWorld().getUID());
        return new SavedVersion(getHeader(), savings, getClass());
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

    @Override public UUID getUUID() {
        return zombie.getUniqueId();
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
        if (game != null && game.hasMob(this)) {
            game.removeObject(this);
        }
        if (zombie != null) {
            zombie.getWorld().playEffect(zombie.getLocation(), Effect.EXTINGUISH, 1);
            zombie.remove();
            zombie = null;
        }
        game.setMobCount(game.getMobCount() - 1);
        data.objects.remove(this);
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
    @Override public void setHealth(double amt) {
        zombie.setHealth(amt);
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

    /**
     * Sets the target of this instance.
     * 
     * @param loc The location to target
     */
    @Override public void setTargetLocation(Location loc) {
        mtt.setTarget(loc);
    }
}
