package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.PermissionManager;
import com.github.jamesnorris.ablockalypse.enumerated.Local;
import com.github.jamesnorris.ablockalypse.enumerated.ZAEffect;

public class SignChange implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player places a block.
     * Used mainly for avoiding unwanted players from placing ZASigns.
     * Also used to add map data storage signs to the arraylist in GlobalData to be loaded on disable. */
    @EventHandler(priority = EventPriority.HIGHEST) public void SCE(SignChangeEvent event) {
        Player player = event.getPlayer();
        // Sign sign = (Sign) event.getBlock().getState();
        String[] lines = event.getLines();
        if (lines[0].equalsIgnoreCase(Local.BASE_STRING.getSetting())) {
            if (data.isZAPlayer(player) || !player.hasPermission(PermissionManager.PLACE_SIGNS)) {
                event.setCancelled(true);
                ZAEffect.FLAMES.play(player.getLocation());
                player.sendMessage(ChatColor.RED + "You do not have permissions to place ZA signs!");
                return;
            } else if (player.hasPermission(PermissionManager.PLACE_SIGNS)) {
                player.sendMessage(ChatColor.GRAY + "You have created a ZA sign.");
                return;
            }
        }
    }
}
