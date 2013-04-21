package com.github.jamesnorris.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.ZAPlayer;

public class PlayerDeath implements Listener {
    private DataContainer data = DataContainer.data;
    
    /*
     * Called when a player is killed.
     * 
     * Used for respawning the player after the current level.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PDE(PlayerDeathEvent event) {
        Player p = event.getEntity();
        if (data.players.containsKey(p)) {
            event.getDrops().clear();
            event.setKeepLevel(true);
            ZAPlayer zap = data.players.get(p);
            zap.setLimbo(true);
            Game zag = zap.getGame();
            // removing perks
            zap.clearPerks();
            // end removing perks
            if (zag.getRemainingPlayers() > 0) {
                if (zap.isInLastStand()) {
                    zap.toggleLastStand();
                }
            } else {
                zag.end();
            }
        }
    }
}
