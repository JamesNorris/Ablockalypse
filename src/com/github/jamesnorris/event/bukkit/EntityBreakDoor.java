package com.github.jamesnorris.event.bukkit;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Undead;

public class EntityBreakDoor implements Listener {
    private DataContainer data = Ablockalypse.getData();

    @EventHandler(priority = EventPriority.HIGHEST) public void EBDE(EntityBreakDoorEvent event) {
        LivingEntity e = event.getEntity();
        if (e instanceof Zombie) {
            Zombie z = (Zombie) e;
            for (Undead gz : data.undead) {
                if (gz.getZombie() == z) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
