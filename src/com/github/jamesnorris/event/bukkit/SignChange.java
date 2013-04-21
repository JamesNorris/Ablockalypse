package com.github.jamesnorris.event.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Local;
import com.github.jamesnorris.enumerated.ZAEffect;

public class SignChange implements Listener {
    private DataContainer data = DataContainer.data;
    
    /*
     * Called when a player places a block.
     * Used mainly for avoiding unwanted players from placing ZASigns.
     * Also used to add map data storage signs to the arraylist in GlobalData to be loaded on disable.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void SCE(SignChangeEvent event) {
        Player player = event.getPlayer();
        // Sign sign = (Sign) event.getBlock().getState();
        String[] lines = event.getLines();
        if (lines[0].equalsIgnoreCase(Local.BASE_STRING.getSetting())) {
            if (data.players.containsKey(player) || !player.hasPermission("za.sign")) {
                event.setCancelled(true);
                ZAEffect.FLAMES.play(player.getLocation());
                player.sendMessage(ChatColor.RED + "You do not have permissions to place ZA signs!");
                return;
            } else if (player.hasPermission("za.sign")) {
                player.sendMessage(ChatColor.GRAY + "You have created a ZA sign.");
                return;
            }
        }
    }
}
