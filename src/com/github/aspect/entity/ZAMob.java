package com.github.aspect.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.PermanentAspect;
import com.github.aspect.block.Barrier;
import com.github.aspect.intelligent.Game;
import com.github.behavior.GameObject;
import com.github.manager.SpawnManager;
import com.github.threading.DelayedTask;
import com.github.threading.RepeatingTask;
import com.github.threading.inherent.MobTargettingTask;
import com.github.utility.BukkitUtility;
import com.github.utility.ranged.type.EntityHitBox;
import com.github.utility.serial.SavedVersion;

public class ZAMob extends PermanentAspect implements GameObject {
    protected static DataContainer data = Ablockalypse.getData();
    private MobTargettingTask targetter;
    private double absorption = 0F;
    protected LivingEntity entity;
    protected Game game;
    private EntityHitBox hitBox;

    public ZAMob(LivingEntity entity, Game game) {
        this.entity = entity;
        this.game = game;
        this.hitBox = new EntityHitBox(entity, 1D, 1D, 1D);
        data.objects.add(hitBox);
        if (entity == null) {
            remove();
            return;
        }
        data.objects.add(this);
        game.addObject(this);
        game.setMobCountSpawnedInThisRound(game.getMobCountSpawnedInThisRound() + 1);
        game.setMobCount(game.getMobCount() + 1);
        retarget();
    }

    public ZAMob(SavedVersion savings) {
        this((LivingEntity) data.getEntityByUUID(Bukkit.getWorld((UUID) savings.get("world_uuid")), (UUID) savings.get("entity_uuid")), Ablockalypse.getData().getGame((String) savings.get("game_name"), true));
        absorption = (Double) savings.get("hit_absorption");
    }
    
    @Override public UUID getUUID() {
        return entity.getUniqueId();
    }
    
    public void setHitBox(EntityHitBox hitBox) {
        data.objects.remove(this.hitBox);
        data.objects.add(hitBox);
        this.hitBox = hitBox;
    }
    
    public EntityHitBox getHitBox() {
        return hitBox;
    }

    /**
     * Gets the Entity instance of the mob.
     * 
     * @return The Entity associated with this instance
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * Gets the game that this mob is in.
     * 
     * @return The game that this mob is in
     */
    public Game getGame() {
        return game;
    }

    public double getHitAbsorption() {
        return absorption;
    }

    /**
     * Gets the targetter for this mob.
     * 
     * @return The targetter attached to this instance
     */
    public MobTargettingTask getTargetter() {
        return targetter;
    }
    
    public void retarget() {
        final Player p = game.getClosestLivingPlayer(entity.getLocation());
        Barrier targetbarrier = SpawnManager.getClosestBarrier(game, p == null ? entity.getLocation() : p.getLocation());
        targetter = new MobTargettingTask(entity, targetbarrier != null ? targetbarrier.getCenter() : p != null ? p.getLocation() : null/*will attempt to retarget*/, 0.1D, true);
        if (targetbarrier == null && p != null) {
            final Location previous = p.getLocation();
            new RepeatingTask(1, true) {
                @Override public void run() {
                    if (p.isDead()) {
                        cancel();
                        return;
                    }
                    if (!BukkitUtility.locationMatch(previous, p.getLocation())) {
                        targetter.setTarget(p.getLocation());
                    }
                }
            };
        }
    }
    
    /**
     * Kills the undead and finalized the instance.
     */
    public void kill() {
        if (game != null && game.hasMob(this)) {
            game.removeObject(this);
        }
        if (entity != null) {
            entity.getWorld().playEffect(entity.getLocation(), Effect.EXTINGUISH, 1);
            new DelayedTask(40, true) {
                @Override public void run() {
                    entity.remove();
                    entity = null;
                }
            };
        }
        data.objects.remove(hitBox);
        data.objects.remove(this);
        game.setMobCount(game.getMobCount() - 1);
    }

    // /**
    // * Sets the zombie health. Mostly used for increasing health through the levels.
    // *
    // * @param amt The amount of health to give to the zombie
    // */
    // public void setHealth(double amt) {
    //
    // }
    public void setHitAbsorption(double absorption) {
        this.absorption = absorption;
    }

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID().toString() + ">";
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("hit_absorption", absorption);
        savings.put("game_name", game.getName());
        savings.put("entity_uuid", entity.getUniqueId());
        savings.put("world_uuid", entity.getWorld().getUID());
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public Block getDefiningBlock() {
        if (entity == null) {
            return null;
        }
        return entity.getLocation().clone().subtract(0, 1, 0).getBlock();
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

    @Override public void remove() {
        kill();
    }

    @Override public void onGameEnd() {
        kill();
    }

    @Override public void onGameStart() {}

    @Override public void onNextLevel() {}

    @Override public void onLevelEnd() {
        kill();//don't know when this would happen, but it doesn't hurt to be safe
    }

    @Override public int getLoadPriority() {
        return 3;
    }
}
