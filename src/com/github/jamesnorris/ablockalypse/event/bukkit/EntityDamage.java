package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;
import com.github.jamesnorris.ablockalypse.aspect.entity.Zombie;

public class EntityDamage implements Listener {
    private DataContainer data = Ablockalypse.getData();
    protected DamageCause[] cancelCauses = new DamageCause[] {DamageCause.SUFFOCATION, DamageCause.LIGHTNING};

    /* Called when an entity is damaged.
     * Used mostly for cancelling fire damage to ZA mobs. */
    @EventHandler(priority = EventPriority.HIGHEST) public void EDE(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity e = (LivingEntity) entity;
        if (e != null && data.isZAMob(e)) {
            if ((event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) && e instanceof org.bukkit.entity.Zombie) {
                Zombie gu = data.getZombie(e);
                if (gu.isFireproof()) {
                    e.setFireTicks(0);
                    event.setCancelled(true);
                }
            } else if (event.getCause() == DamageCause.FALL && event.getDamage() <= 2) {
                event.setCancelled(true);
            } else if (shouldBeCancelled(event.getCause())) {
                event.setCancelled(true);
            }
        } else if (e != null && e instanceof Player) {
            Player player = (Player) e;
            if (data.isZAPlayer(player)) {
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
