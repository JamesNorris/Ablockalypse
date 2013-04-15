package com.github.jamesnorris.event.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.implementation.Undead;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.util.Breakable;

public class EntityDamage extends DataManipulator implements Listener {
    protected DamageCause[] cancelCauses = new DamageCause[] {DamageCause.SUFFOCATION, DamageCause.LIGHTNING};
    /*
     * Called when an entity is damaged.
     * Used mostly for cancelling fire damage to ZA mobs.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void EDE(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (e != null && data.isZAMob(e)) {
            if ((event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) && e instanceof Zombie) {
                Undead gu = (Undead) data.getUndead(e);
                if (gu.isFireproof()) {
                        Breakable.getNMSEntity(e).extinguish();
                    event.setCancelled(true);
                }
            } else if (shouldBeCancelled(event.getCause())) {
                event.setCancelled(true);
            }
        } else if (e != null && e instanceof Player) {
            Player player = (Player) e;
            if (data.playerExists(player)) {
                ZAPlayer zap = data.getZAPlayer(player);
                if (zap.isInLastStand()) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    protected boolean shouldBeCancelled(DamageCause cause) {
        for (DamageCause dmg : cancelCauses) {
            if (dmg == cause) {
                return true;
            }
        }
        return false;
    }
}
