package com.github.event.bukkit;

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

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Claymore;

public class BlockBreak implements Listener {
    private static DataContainer data = Ablockalypse.getData();

    public static boolean shouldBeBroken(Material type) {
        for (Material m : data.modifiableMaterials) {
            if (m == type) {
                return true;
            }
        }
        return false;
    }

    /* Called when a player breaks a block.
     * Mainly used for preventing ZA Players from breaking blocks while in-game. */
    @EventHandler(priority = EventPriority.HIGHEST) public void BBE(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Block b = event.getBlock();
        Location loc = b.getLocation();
        if (data.isZAPlayer(p) && !BlockBreak.shouldBeBroken(b.getType())) {
            event.setCancelled(true);
        } else if ((b.getType() == Material.FLOWER_POT || b.getType() == Material.FLOWER_POT_ITEM) && data.isClaymore(loc)) {
            Claymore more = data.getClaymore(loc);
            if (more.getPlacer().getPlayer().getName().equalsIgnoreCase(p.getName())) {
                Ablockalypse.getExternal().getItemFileManager().giveItem(p, new ItemStack(Material.FLOWER_POT_ITEM, 1));
                p.sendMessage(ChatColor.GRAY + "You have picked up your claymore.");
            } else {
                p.sendMessage(ChatColor.RED + "That is not your claymore!");
                event.setCancelled(true);
            }
        }
    }
}
