package com.github.jamesnorris.ablockalypse.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.behavior.GameAspect;
import com.github.jamesnorris.ablockalypse.behavior.Targettable;
import com.github.jamesnorris.ablockalypse.threading.DelayedTask;
import com.github.jamesnorris.ablockalypse.threading.inherent.MobTargetterTask;
import com.github.jamesnorris.ablockalypse.utility.SpawnUtility;
import com.github.jamesnorris.mcshot.type.EntityHitBox;

public class ZAMob extends ZALiving implements GameAspect {
    protected static DataContainer data = Ablockalypse.getData();
    private MobTargetterTask targetter;
    private double absorption = 0F;
    protected LivingEntity entity;
    protected Game game;
    private EntityHitBox hitBox;

    public ZAMob(LivingEntity entity, Game game) {
        super(entity);
        this.entity = super.getEntity();
        this.game = game;
        if (entity == null) {
            remove();
            return;
        }
        hitBox = new EntityHitBox(entity, getLength(), getWidth(), getHeight());
        data.objects.add(hitBox);
        data.objects.add(this);
        game.addObject(this);
        game.setMobCountSpawnedInThisRound(game.getMobCountSpawnedInThisRound() + 1);
        game.setMobCount(game.getMobCount() + 1);
        targetter = new MobTargetterTask(this, (Targettable) null, true);
        retarget();
    }

    public ZAMob(Map<String, Object> savings) {
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
    @Override public LivingEntity getEntity() {
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

    public double getHeight() {
        return 1D;
    }

    public double getHitAbsorption() {
        return absorption;
    }

    public EntityHitBox getHitBox() {
        return hitBox;
    }

    public double getLength() {
        return 1D;
    }

    @Override public int getLoadPriority() {
        return 3;
    }

    @Override public Map<String, Object> getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("hit_absorption", absorption);
        savings.put("game_name", game.getName());
        savings.put("entity_uuid", entity.getUniqueId());
        savings.put("world_uuid", entity.getWorld().getUID());
        return savings;
    }

    /* In nodes per tick */
    public double getSpeed() {
        return 1D;
    }

    /**
     * Gets the targetter for this mob.
     * 
     * @return The targetter attached to this instance
     */
    public MobTargetterTask getTargetter() {
        return targetter;
    }

    @Override public UUID getUUID() {
        return entity.getUniqueId();
    }

    public double getWidth() {
        return 1D;
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
                    if (entity != null) {
                        entity.remove();
                        entity = null;
                    }
                }
            };
        }
        if (hitBox != null) {
            data.objects.remove(hitBox);
        }
        data.objects.remove(this);
        game.setMobCount(game.getMobCount() - 1);
    }
    
    public boolean isValid() {
        return entity != null && !entity.isDead();
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

    public void retarget() {
        Player player = game.getClosestLivingPlayer(entity.getLocation());
        Barrier barrier = SpawnUtility.getClosestBarrier(game, player == null || !data.isZAPlayer(player) ? entity.getLocation() : player.getLocation());
        Targettable target = (barrier != null && barrier.isResponsive()) && (player == null || !data.isZAPlayer(player) || barrier.getCenter().distance(entity.getLocation()) < player.getLocation().distance(entity.getLocation())) ? barrier : data.getZAPlayer(player);
        targetter.setTarget(target);
    }

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
