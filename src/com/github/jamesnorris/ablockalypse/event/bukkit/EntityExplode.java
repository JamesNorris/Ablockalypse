package com.github.jamesnorris.ablockalypse.event.bukkit;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;
import com.github.jamesnorris.ablockalypse.utility.BukkitUtility;

public class EntityExplode implements Listener {
    private static HashMap<UUID, Boolean> uuids = new HashMap<UUID, Boolean>();// UUID, whether or not to cancel the event entirely

    public static void createNonBlockDesructionExplosion(Location loc, float yield) {
        loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), yield, false, false);
    }

    public static void createNonBlockDestructionExplosionWithPoints(ZAPlayer zap, Location loc, float yield) {
        createNonBlockDesructionExplosion(loc, yield);
        for (Entity ent : BukkitUtility.getNearbyEntities(loc, yield, yield, yield)) {
            EntityDamageEvent lastDamage = ent.getLastDamageCause();
            if (ent instanceof LivingEntity && !(ent instanceof Player) && lastDamage != null && lastDamage.getCause() == DamageCause.ENTITY_EXPLOSION) {
                EntityDeath.behaveLikeKill(zap.getPlayer(), ent);
            }
        }
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

    /* Called when an entity explodes.
     * Used mainly to prevent grenades from doing damage to land. */
    @EventHandler(priority = EventPriority.HIGHEST) public void EEE(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        if (uuids.containsKey(e.getUniqueId()) && uuids.get(e.getUniqueId())) {
            event.setCancelled(true);
            uuids.remove(e.getUniqueId());
        } else if (uuids.containsKey(e.getUniqueId())) {
            event.blockList().clear();
            uuids.remove(e.getUniqueId());
        }
    }
}
