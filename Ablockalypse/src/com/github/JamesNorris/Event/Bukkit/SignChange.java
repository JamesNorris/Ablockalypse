package com.github.JamesNorris.Event.Bukkit;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.External;
import com.github.JamesNorris.MessageTransfer;
import com.github.JamesNorris.Data.MapDataStorage;
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
        } else if (lines[1].equalsIgnoreCase(Local.MAPDATASTORAGESTRING.getSetting())) {
            data.mapDataSigns.put(sign.getLocation(), lines);
            SpecificMessage sm = new SpecificMessage(MessageDirection.PLAYER_PRIVATE, ChatColor.GRAY + "Map data created, all data will be saved to a file on stop. \nPlease note that this data is only current, and never updates.");
            sm.setExceptionBased(false);
            sm.addTarget(player.getName());
            MessageTransfer.sendMessage(sm);
            return;
        } else if (lines[1].equalsIgnoreCase(Local.MAPDATALOADSTRING.getSetting())) {
            String oldFile = lines[2] + "_mapdata.bin";
            File saveFile = new File(Ablockalypse.instance.getDataFolder(), File.separatorChar + "map_data" + File.separatorChar + oldFile);
            try {
                MapDataStorage mds = (MapDataStorage) External.load(saveFile.getPath());
                mds.loadToGame(data.findGame(lines[2]), sign.getLocation());
                SpecificMessage sm = new SpecificMessage(MessageDirection.PLAYER_PRIVATE, ChatColor.GRAY + "Map data loaded! \nPlease note that this is a single event, and is not constantly updated/loaded.");
                sm.setExceptionBased(false);
                sm.addTarget(player.getName());
                MessageTransfer.sendMessage(sm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
    }
}
