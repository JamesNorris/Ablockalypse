package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.MessageTransfer;
import com.github.JamesNorris.Enumerated.Local;
import com.github.JamesNorris.Enumerated.MessageDirection;
import com.github.JamesNorris.Enumerated.ZAEffect;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.SpecificMessage;

public class SignChange extends DataManipulator implements Listener {
    /*
     * Called when a player places a block.
     * Used mainly for avoiding unwanted players from placing ZASigns.
     * Also used to add map data storage signs to the arraylist in GlobalData to be loaded on disable.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void SCE(SignChangeEvent event) {
        Player player = event.getPlayer();
        Sign sign = (Sign) event.getBlock().getState();
        String[] lines = event.getLines();
        if (data.players.containsKey(player) || !player.hasPermission("za.sign")) {
            if (lines[0].equalsIgnoreCase(Local.BASESTRING.getSetting())) {
                event.setCancelled(true);
                EffectUtil.generateEffect(player, ZAEffect.FLAMES);
                SpecificMessage sm = new SpecificMessage(MessageDirection.PLAYER_PRIVATE, ChatColor.RED + "You do not have permissions to place ZA signs!");
                sm.setExceptionBased(false);
                sm.addTarget(player.getName());
                MessageTransfer.sendMessage(sm);
                return;
            }
        } else if (lines[1].equalsIgnoreCase(Local.MAPDATASTORAGESTRING.getSetting()) && player.hasPermission("za.create")) {
            data.mapDataSigns.put(sign.getLocation(), lines);
            SpecificMessage sm = new SpecificMessage(MessageDirection.PLAYER_PRIVATE, ChatColor.GRAY + "Map data queued, all data will be saved to a file on stop. \nPlease note that this data is only a snapshot, and never updates automatically.");
            sm.setExceptionBased(false);
            sm.addTarget(player.getName());
            MessageTransfer.sendMessage(sm);
            return;
        }
    }
}
