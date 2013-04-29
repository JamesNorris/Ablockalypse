package com.github.jamesnorris.event.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.ZAPlayer;

public class EntityTarget implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when an entity targets another entity.
     * Mostly used for making sure non-supported entites do not attack ZA players. */
    @EventHandler(priority = EventPriority.HIGHEST) public void ETE(EntityTargetEvent event) {
        Entity target = event.getTarget();
        Entity entity = event.getEntity();
        if (target instanceof Player) {
            Player p = (Player) target;
            if (data.players.containsKey(p)) {
                ZAPlayer zap = data.players.get(p);
                if (zap.isInLastStand() || !data.isZAMob(entity)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
