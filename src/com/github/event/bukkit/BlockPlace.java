package com.github.event.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.block.Claymore;
import com.github.aspect.entity.ZAPlayer;

public class BlockPlace implements Listener {
    private static DataContainer data = Ablockalypse.getData();

    public static boolean shouldBePlaced(Material type) {
        for (Material m : data.modifiableMaterials) {
            if (m == type) {
                return true;
            }
        }
        return false;
    }

    /* Called when a player places a block.
     * Mainly used to create claymores. */
    @EventHandler(priority = EventPriority.HIGHEST) public void BPE(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Block b = event.getBlockPlaced();
        if (data.isZAPlayer(p) && (b.getType() == Material.FLOWER_POT || b.getType() == Material.FLOWER_POT_ITEM)) {// See PlayerInteract.java to allow placement
            ZAPlayer zap = data.getZAPlayer(p);
            Location loc = b.getLocation();
            new Claymore(loc, zap.getGame(), zap);
            p.sendMessage(ChatColor.GRAY + "You have placed a claymore.");
        }
    }
}
