package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;

public class CreatureSpawn implements Listener {
    private DataContainer data = Ablockalypse.getData();

    @EventHandler(priority = EventPriority.HIGHEST) public void CSE(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(Boolean) Setting.PREVENT_NEARBY_SPAWNING.getSetting() || entity instanceof Player || !(entity instanceof LivingEntity) || data.isZAMob((LivingEntity) entity)) {
            return;
        }
        for (ZAPlayer zap : data.getObjectsOfType(ZAPlayer.class)) {
            if (!entity.getLocation().getWorld().getName().equals(zap.getPlayer().getWorld().getName())) {
                continue;
            }
            if (entity.getLocation().distanceSquared(zap.getPlayer().getLocation()) <= Math.pow((Integer) Setting.PREVENT_NEARBY_SPAWNING_RADIUS.getSetting(), 2)) {
                event.setCancelled(true);
            }
        }
    }
}
