package com.github.event.bukkit;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;

public class VehicleExit implements Listener {
    private DataContainer data = Ablockalypse.getData();
    
    @EventHandler(priority = EventPriority.HIGHEST) public void VEE(VehicleExitEvent event) {
        LivingEntity ent = event.getExited();
        if (ent instanceof Player && data.isZAPlayer((Player) ent) && data.getZAPlayer((Player) ent).isInLastStand()) {
            event.setCancelled(true);
        }
    }
}
