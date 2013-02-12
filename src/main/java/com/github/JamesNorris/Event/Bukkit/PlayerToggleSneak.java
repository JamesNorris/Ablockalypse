package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Implementation.GameBarrier;

public class PlayerToggleSneak extends DataManipulator implements Listener {
    /*
     * Called when a player changes from walking to sneaking.
     * Used mostly for repairing broken barriers.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PTSE(PlayerToggleSneakEvent event) {
        Player p = event.getPlayer();
        if (data.players.containsKey(p)) {
            if (data.players.get(p).isInLastStand())
                event.setCancelled(true);
            for (GameBarrier b : data.barrierpanels.keySet())
                if (b.isWithinRadius(p) && b.isBroken()) {
                    b.fixBarrier(p);
                    break;
                }
        }
    }
}
