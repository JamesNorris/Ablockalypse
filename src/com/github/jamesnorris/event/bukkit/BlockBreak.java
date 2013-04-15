package com.github.jamesnorris.event.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.External;
import com.github.jamesnorris.implementation.Claymore;

public class BlockBreak extends DataManipulator implements Listener {
    /*
     * Called when a player breaks a block.
     * Mainly used for preventing ZA Players from breaking blocks while in-game.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void BBE(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Block b = event.getBlock();
        Location loc = b.getLocation();
        if (DataManipulator.data.playerExists(p) && !BlockBreak.shouldBeBroken(b.getType())) {
            event.setCancelled(true);
        } else if ((b.getType() == Material.FLOWER_POT || b.getType() == Material.FLOWER_POT_ITEM) && data.isClaymore(loc)) {
            Claymore more = data.getClaymore(loc);
            if (more.getPlacer().getPlayer().getName().equalsIgnoreCase(p.getName())) {
                External.itemManager.giveItem(p, new ItemStack(Material.FLOWER_POT_ITEM, 1));
                p.sendMessage(ChatColor.GRAY + "You have picked up your claymore.");
            } else {
                p.sendMessage(ChatColor.RED + "That is not your claymore!");
                event.setCancelled(true);
            }
        }
    }
    
    public static boolean shouldBeBroken(Material type) {
        for (Material m : data.modifiableMaterials) {
            if (m == type) {
                return true;
            }
        }
        return false;
    }
}
