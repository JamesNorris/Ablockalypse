package com.github.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Barrier;
import com.github.aspect.ZAPlayer;

public class PlayerToggleSneak implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player changes from walking to sneaking.
     * Used mostly for repairing broken barriers. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PTSE(PlayerToggleSneakEvent event) {
        Player p = event.getPlayer();
        if (data.isZAPlayer(p)) {
            ZAPlayer zap = data.getZAPlayer(p);
            if (zap.isInLastStand()) {
                event.setCancelled(true);
                return;
            }
            for (Barrier b : zap.getGame().getObjectsOfType(Barrier.class)) {
                if (b.isWithinRadius(p, 3) && b.isBroken()) {
                    b.fixBarrier(zap);
                    break;
                }
            }
        }
    }
}
