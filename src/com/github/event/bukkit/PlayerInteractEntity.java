package com.github.event.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.ZAPlayer;
import com.github.threading.inherent.LastStandPickupThread;

public class PlayerInteractEntity implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* The event called when a player hits another entity.
     * 
     * Used for picking a player up out of last stand. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PIEE(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        Entity e = event.getRightClicked();
        if (data.isZAPlayer(p) && e instanceof Player && data.isZAPlayer((Player) e)) {
            ZAPlayer zap = data.getZAPlayer((Player) e);
            ZAPlayer zap2 = data.getZAPlayer(p);
            if (zap.isInLastStand()) {
                new LastStandPickupThread(zap2, zap, 20, 5, true);
            }
        }
    }
}
