package com.github.jamesnorris.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.ZAPlayer;

public class PlayerToggleSneak implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player changes from walking to sneaking.
     * Used mostly for repairing broken barriers. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PTSE(PlayerToggleSneakEvent event) {
        Player p = event.getPlayer();
        if (data.players.containsKey(p)) {
            ZAPlayer zap = data.players.get(p);
            if (zap.isInLastStand()) {
                event.setCancelled(true);
            }
            for (Barrier b : data.barriers) {
                if (b.isWithinRadius(p, 2) && b.isBroken()) {
                    b.fixBarrier(zap);
                    break;
                }
            }
        }
    }
}
