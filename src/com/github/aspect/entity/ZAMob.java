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
import com.github.behavior.GameAspect;
import com.github.manager.SpawnManager;
import com.github.threading.DelayedTask;
import com.github.threading.RepeatingTask;
import com.github.threading.inherent.MobTargettingTask;
import com.github.utility.BukkitUtility;
import com.github.utility.Pathfinder;
import com.github.utility.ranged.type.EntityHitBox;
import com.github.utility.serial.SavedVersion;

public class ZAMob extends PermanentAspect implements GameAspect {
    protected static DataContainer data = Ablockalypse.getData();
    private MobTargettingTask targetter;
    private double absorption = 0F;
    protected LivingEntity entity;
    protected Game game;
    private EntityHitBox hitBox;

    public ZAMob(LivingEntity entity, Game game) {
        this.entity = entity;
        this.game = game;
        if (entity == null) {
            remove();
            return;
        }
        hitBox = new EntityHitBox(entity, 1D, 1D, 1D);
        data.objects.add(hitBox);
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
    @Override public Game getGame() {
        return game;
    }

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID().toString() + ">";
    }

    public double getHitAbsorption() {
        return absorption;
    }

    public EntityHitBox getHitBox() {
        return hitBox;
    }

    @Override public int getLoadPriority() {
        return 3;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("hit_absorption", absorption);
        savings.put("game_name", game.getName());
        savings.put("entity_uuid", entity.getUniqueId());
        savings.put("world_uuid", entity.getWorld().getUID());
        return new SavedVersion(getHeader(), savings, getClass());
    }

    /**
     * Gets the targetter for this mob.
     * 
     * @return The targetter attached to this instance
     */
    public MobTargettingTask getTargetter() {
        return targetter;
    }

    @Override public UUID getUUID() {
        return entity.getUniqueId();
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
        if (hitBox != null) {
            data.objects.remove(hitBox);
        }
        data.objects.remove(this);
        game.setMobCount(game.getMobCount() - 1);
    }

    @Override public void onGameEnd() {
        kill();
    }

    @Override public void onGameStart() {}

    @Override public void onLevelEnd() {
        kill();// don't know when this would happen, but it doesn't hurt to be safe
    }

    @Override public void onNextLevel() {}

    @Override public void remove() {
        kill();
    }

    @SuppressWarnings("deprecation") public void retarget() {
        Player p = game.getClosestLivingPlayer(entity.getLocation());
        if (p != null && Pathfinder.calculate(entity.getLocation(), p.getLocation()).getTotalHeuristic() >= entity.getLocation().distance(p.getLocation()) * 3) {
            p = null;
        }
        Barrier targetbarrier = SpawnManager.getClosestBarrier(game, p == null ? entity.getLocation() : p.getLocation());
        Location target = p != null ? p.getLocation() : targetbarrier != null ? targetbarrier.getCenter() : null;
        if (targetter == null) {
            targetter = new MobTargettingTask(entity, target, 0.1D, true);
        } else {
            targetter.setTarget(target);
        }
        if (p != null) {
            final Player targetPlayer = p;
            final Location previous = p.getLocation();
            new RepeatingTask(1, true) {
                @Override public void run() {
                    if (targetPlayer.isDead()) {
                        cancel();
                        return;
                    }
                    if (!BukkitUtility.locationMatch(previous, targetPlayer.getLocation())) {
                        targetter.setTarget(targetPlayer.getLocation());
                    }
                }
            };
        }
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

    public void setHitBox(EntityHitBox hitBox) {
        data.objects.remove(this.hitBox);
        this.hitBox = hitBox;
        if (hitBox == null) {
            return;
        }
        data.objects.add(hitBox);
    }
}
