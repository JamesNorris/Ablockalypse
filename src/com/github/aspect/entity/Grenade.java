package com.github.aspect.entity;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.aspect.NonspecificGameAspect;
import com.github.enumerated.ZAEffect;
import com.github.event.bukkit.EntityExplode;
import com.github.threading.RepeatingTask;
import com.github.utility.AblockalypseUtility;

public class Grenade extends NonspecificGameAspect {// does not need to be a gameobject, as the explosion is linked to the player that throws the grenade.
    private float yield = 2F;// Can be changed to make a larger explosion.
    private Entity entity;
    private ZAPlayer owner;
    private boolean live = true, sticky = false;
    private Vector momentum;

    public Grenade(Entity entity, ZAPlayer owner) {
        this(entity, owner, 60, true, false, new Vector(0, 0, 0));
    }

    public Grenade(Entity entity, ZAPlayer owner, int countdownTicks, boolean live, boolean sticky) {
        this(entity, owner, countdownTicks, live, sticky, new Vector(0, 0, 0));
    }

    /* 1 > momentumX, Y, Z > -1
     * percentage of force towards that direction */
    public Grenade(Entity entity, ZAPlayer owner, int countdownTicks, boolean live, boolean sticky, Vector momentum) {
        super(entity.getLocation());
        this.entity = entity;
        entity.setTicksLived(Integer.MAX_VALUE);
        this.owner = owner;
        this.live = live;
        this.momentum = momentum;
        triggerPhysics(countdownTicks);
    }

    public Entity getGrenadeEntity() {
        return entity;
    }

    public ZAPlayer getOwner() {
        return owner;
    }

    public float getYield() {
        return yield;
    }

    public boolean isLive() {
        return live;
    }

    @Override public void remove() {
        if (entity != null) {
            entity.remove();
            entity = null;
        }
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public void setYield(float yield) {
        this.yield = yield;
    }

    private Item createGroundGrenade() {
        Item ground = entity.getWorld().dropItem(entity.getLocation(), new ItemStack(Material.ENDER_PEARL, 1));
        ground.setVelocity(momentum);
        ground.setTicksLived(Integer.MAX_VALUE);
        ground.setPickupDelay(Integer.MAX_VALUE);
        return ground;
    }

    private void triggerPhysics(final int ticks) {
        final Grenade grenade = this;
        final Item groundGrenade = createGroundGrenade();
        new RepeatingTask(1, true) {
            int time = ticks;
            RepeatingTask warning;

            @Override public void cancel() {
                if (!sticky) {// TODO if sticky, stick to the hit object, and follow it while it is moving
                    if (entity != null) {
                        entity.teleport(groundGrenade.getLocation());
                    }
                }
                if (live) {
                    EntityExplode.createNonBlockDestructionExplosionWithPoints(owner, groundGrenade.getLocation(), yield);
                    live = false;
                } else {
                    ZAEffect.EXTINGUISH.play(groundGrenade.getLocation());
                }
                warning.cancel();
                groundGrenade.remove();
                grenade.remove();
            }

            @Override public void run() {
                --time;
                if (time <= 0 || entity == null) {
                    cancel();
                    return;
                }
                if (warning != null) {
                    warning.cancel();
                }
                warning = AblockalypseUtility.scheduleNearbyWarning(groundGrenade.getLocation(), ChatColor.GRAY + "Press " + ChatColor.AQUA + "SHIFT" + ChatColor.GRAY
                        + " to pick up grenade.", 2, 3.5, 2, 10000);
            }
        };
    }
}
