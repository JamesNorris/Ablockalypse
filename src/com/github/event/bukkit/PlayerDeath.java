package com.github.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Game;
import com.github.aspect.ZAPlayer;
import com.github.enumerated.PlayerStatus;

public class PlayerDeath implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player is killed.
     * 
     * Used for respawning the player after the current level. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PDE(PlayerDeathEvent event) {// RespawnThread is activated by PlayerRespawn.java
        Player p = event.getEntity();
        if (data.isZAPlayer(p)) {
            event.getDrops().clear();
            event.setKeepLevel(true);
            ZAPlayer zap = data.getZAPlayer(p);
            zap.setStatus(PlayerStatus.LIMBO);
            Game zag = zap.getGame();
            zap.clearPerks();// remove perks
            if (zag.getRemainingPlayers() > 0) {
                if (zap.isInLastStand()) {
                    zap.toggleLastStand();
                }
            } else {
                zag.end(false);
            }
        }
    }
}
