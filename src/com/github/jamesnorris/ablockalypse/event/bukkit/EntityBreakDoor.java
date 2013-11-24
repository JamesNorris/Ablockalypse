package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;

public class EntityBreakDoor implements Listener {
    private DataContainer data = Ablockalypse.getData();

    @EventHandler(priority = EventPriority.HIGHEST) public void EBDE(EntityBreakDoorEvent event) {
        LivingEntity e = event.getEntity();
        if (data.isZAMob(e)) {
            event.setCancelled(true);
        }
    }
}
