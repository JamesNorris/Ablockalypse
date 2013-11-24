package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;

public class EntityTarget implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when an entity targets another entity.
     * Mostly used for making sure non-supported entites do not attack ZA players. */
    @EventHandler(priority = EventPriority.HIGHEST) public void ETE(EntityTargetEvent event) {
        Entity target = event.getTarget();
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity && target instanceof Player) {
            Player p = (Player) target;
            if (data.isZAPlayer(p)) {
                ZAPlayer zap = data.getZAPlayer(p);
                if (zap.isInLastStand() || !data.isZAMob((LivingEntity) entity)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
