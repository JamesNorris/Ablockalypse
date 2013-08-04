package com.github.event.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.ZAPlayer;

public class EntityExplode implements Listener {
    private static HashMap<UUID, Boolean> uuids = new HashMap<UUID, Boolean>();// UUID, whether or not to cancel the event entirely

    public static void createNonBlockDestructionExplosion(Location loc, float yield) {
        World w = loc.getWorld();
        Entity ent = w.spawnEntity(loc, EntityType.FIREBALL);
        Fireball f = (Fireball) ent;
        UUID uuid = f.getUniqueId();
        uuids.put(uuid, false);
        f.setDirection(new Vector(0, -w.getHighestBlockYAt(loc), 0));
        f.setYield(yield);
        f.setIsIncendiary(true);
        f.setTicksLived(1);
    }

    public static void createNonBlockDestructionExplosionWithPoints(ZAPlayer zap, Location loc, float yield) {
        World w = loc.getWorld();
        Entity ent = w.spawnEntity(loc, EntityType.FIREBALL);
        Fireball f = (Fireball) ent;
        UUID uuid = f.getUniqueId();
        preventBlockDestructionWithPoints(zap, uuid);
        f.setDirection(new Vector(0, -w.getHighestBlockYAt(loc), 0));
        f.setYield(yield);
        f.setIsIncendiary(true);
        f.setTicksLived(1);
    }

    public static void preventBlockDestructionWithPoints(ZAPlayer zap, UUID uuid) {
        ProjectileHit.preventBlockDestructionWithPoints(zap, uuid);
    }

    /**
     * Prevents an explosion from happening or from damaging blocks.
     * 
     * @param uuid The UUID of the entity exploding
     * @param complete True if the event should be completely cancelled, otherwise the blocklist is cleared
     */
    public static void preventExplosion(UUID uuid, boolean complete) {
        uuids.put(uuid, complete);
    }

    private DataContainer data = Ablockalypse.getData();

    /* Called when an entity explodes.
     * Used mainly to prevent grenades from doing damage to land. */
    @EventHandler(priority = EventPriority.HIGHEST) public void EEE(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        if (ProjectileHit.uuids.containsKey(e.getUniqueId())) {
            event.blockList().clear();
            double yield = event.getYield();
            ArrayList<LivingEntity> damaged = new ArrayList<LivingEntity>();
            for (Entity ent : e.getNearbyEntities(yield, yield, yield)) {
                EntityDamageEvent lastDamage = ent.getLastDamageCause();
                if (ent instanceof LivingEntity && !(ent instanceof Player) && lastDamage != null && lastDamage.getCause() == DamageCause.ENTITY_EXPLOSION) {
                    damaged.add((LivingEntity) ent);
                }
            }
            Player p = Bukkit.getPlayer(ProjectileHit.uuids.get(e.getUniqueId()));
            if (data.isZAPlayer(p)) {
                for (LivingEntity le : damaged) {
                    EntityDeath.behaveLikeKill(p, le);
                }
            }
            ProjectileHit.uuids.remove(e.getUniqueId());
        }
        if (uuids.containsKey(e.getUniqueId()) && uuids.get(e.getUniqueId())) {
            event.setCancelled(true);
            uuids.remove(e.getUniqueId());
        } else if (uuids.containsKey(e.getUniqueId())) {
            event.blockList().clear();
            uuids.remove(e.getUniqueId());
        }
    }
}
