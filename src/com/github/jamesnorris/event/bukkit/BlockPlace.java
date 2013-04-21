package com.github.jamesnorris.event.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Claymore;
import com.github.jamesnorris.implementation.ZAPlayer;

public class BlockPlace implements Listener {
    private static DataContainer data = DataContainer.data;
    
    /*
     * Called when a player places a block.
     * Mainly used to create claymores.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void BPE(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Block b = event.getBlockPlaced();
        if (data.players.containsKey(p) && (b.getType() == Material.FLOWER_POT || b.getType() == Material.FLOWER_POT_ITEM)) {//See PlayerInteract.java to allow more blocks to be placed
            ZAPlayer zap = data.players.get(p);
            new Claymore(b, zap.getGame(), zap, true);
            p.sendMessage(ChatColor.GRAY + "You have placed a claymore.");
        }
    }
    
    public static boolean shouldBePlaced(Material type) {
        for (Material m : data.modifiableMaterials) {
            if (m == type) {
                return true;
            }
        }
        return false;
    }
}
