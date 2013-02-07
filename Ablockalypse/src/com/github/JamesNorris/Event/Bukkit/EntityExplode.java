package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.Setting;

public class EntityExplode extends DataManipulator implements Listener {
    /*
     * Called when an entity explodes.
     * Used mainly to prevent grenades from doing damage to land.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void EEE(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        if (e.getType() == EntityType.FIREBALL && ProjectileHit.uuids.containsKey(e.getUniqueId())) {
            event.blockList().clear();
            Player p = (Bukkit.getPlayer(ProjectileHit.uuids.get(e.getUniqueId())));
            if (data.playerExists(p))
                data.getZAPlayer(p).addPoints((Integer) Setting.KILLPOINTINCREASE.getSetting());
            ProjectileHit.uuids.remove(e.getUniqueId());
        }
    }
}
