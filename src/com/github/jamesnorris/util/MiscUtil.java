package com.github.jamesnorris.util;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.jamesnorris.External;
import com.github.jamesnorris.enumerated.PowerupType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZAScheduledTask;

/**
 * The class for all utility methods. This class can be used for any miscellaneous needs of the plugin.
 */
public class MiscUtil {
    private static Random rand;

    /**
     * Drops an item in the direction of the player, then has them pick it up.
     * 
     * @param from The location to drop from
     * @param item The item to drop
     * @param player The player to drop at
     * @param dropDelay The delay before the item is dropped at the player
     */
    public static void dropItemAtPlayer(final Location from, final ItemStack item, final Player player, int dropDelay) {
        Ablockalypse.getMainThread().scheduleDelayedTask(new ZAScheduledTask() {
            @Override public void run() {
                Item i = from.getWorld().dropItem(from, item);
                i.setPickupDelay(Integer.MAX_VALUE);
                final ItemStack is = i.getItemStack();
                final Item finali = i;
                Ablockalypse.getMainThread().scheduleDelayedTask(new ZAScheduledTask() {
                    @Override public void run() {
                        finali.remove();
                        External.itemManager.giveItem(player, is);
                    }
                }, 10);
            }           
        }, dropDelay);
    }

    public static void setChestOpened(Player player, Block block, boolean opened) {
        if (block == null)
            return;
        if (!(block.getState() instanceof Chest))
            return;
        player.playNote(block.getLocation(), (byte) 1, (opened) ? (byte) 1 : (byte) 0);
        if (isDoubleChest(block)) {
            player.playNote(getSecondChest(block).getLocation(), (byte) 1, (opened) ? (byte) 1 : (byte) 0);
        }
    }

    public static void setChestOpened(List<Player> players, Block block, boolean opened) {
        if (block == null)
            return;
        if (!(block.getState() instanceof Chest))
            return;
        for (Player player : players) {
            player.playNote(block.getLocation(), (byte) 1, (opened) ? (byte) 1 : (byte) 0);
            if (isDoubleChest(block)) {
                player.playNote(getSecondChest(block).getLocation(), (byte) 1, (opened) ? (byte) 1 : (byte) 0);
            }
        }
    }

    public static boolean isDoubleChest(Block block) {
        if (block == null)
            return false;
        if (!(block.getState() instanceof Chest))
            return false;
        Chest chest = (Chest) block.getState();
        return chest.getInventory().getContents().length == 54;
    }

    public static boolean anyItemRegulationsBroken(ZAPlayer zap, int id, int cost) {// TODO level? duh
        Player player = zap.getPlayer();
        int points = zap.getPoints();
        if (zap.getPoints() < cost) {
            player.sendMessage(ChatColor.RED + "You have " + points + " / " + cost + " points to buy this.");
            return true;
        }
        return false;
    }

    public static boolean locationMatch(Location loc1, Location loc2) {
        if (loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ())
            return true;
        return false;
    }

    public static boolean locationMatch(Location loc1, Location loc2, int distance) {
        return loc1.distance(loc2) <= distance;
    }

    /**
     * Gets the second chest next to the location of the block given.
     * 
     * @param b The block to check around
     * @return The second block
     */
    public static Block getSecondChest(Block b) {
        BlockFace[] faces = new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        for (BlockFace face : faces) {
            Block bl = b.getRelative(face);
            if (bl.getState() instanceof Chest || bl.getState() instanceof DoubleChest)
                return bl;
        }
        return null;
    }

    public static String getLocationAsString(String prefix, Location loc) {
        return prefix + ": " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ();
    }

    public static int[] swords = new int[] {268, 283, 272, 267, 276};

    public static boolean isEnchantableLikeSwords(ItemStack item) {
        for (int id : swords) {
            if (item.getTypeId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Searched for a random powerup.
     * 
     * @param zap The player to apply the powerup to
     * @param cause The entity that originated this event
     */
    public static void randomPowerup(ZAPlayer zap, Entity cause) {
        if (rand == null)
            rand = new Random();
        int chance = rand.nextInt(100) + 1;
        if (chance <= (Integer) Setting.POWERUP_CHANCE.getSetting()) {
            zap.givePowerup(PowerupType.getById(rand.nextInt(5) + 1), cause);
        }
    }
}
