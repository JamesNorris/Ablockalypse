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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.jamesnorris.enumerated.PowerupType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZAScheduledTask;

public class MiscUtil {
    public static int[] swords = new int[] {268, 283, 272, 267, 276};
    private static Random rand = new Random();

    public static boolean anyItemRegulationsBroken(ZAPlayer zap, int id, int cost) {// TODO level? duh
        Player player = zap.getPlayer();
        int points = zap.getPoints();
        if (zap.getPoints() < cost) {
            player.sendMessage(ChatColor.RED + "You have " + points + " / " + cost + " points to buy this.");
            return true;
        }
        return false;
    }
    
    public static String getLastMethodCalls(Thread thread, int number) {
        StackTraceElement[] stackTraceElements = thread.getStackTrace();
        StringBuilder sb = new StringBuilder();
        int start = 2;
        for (int i = start; i <= number + (start - 1); i++) {
            sb.append(stackTraceElements[i] + "\n");
        }
        return sb.toString();
    }
    
    public static Location findLocationNear(Location loc, int min, int max) {
        if (min > max) {
            final int tempMin = min;
            final int tempMax = max;
            max = tempMin;
            min = tempMax;
        }
        int modX = (rand.nextBoolean() ? 1 : -1) * rand.nextInt(max - min) + min;
        int modZ = (rand.nextBoolean() ? 1 : -1) * rand.nextInt(max - min) + min;
        return loc.clone().add(modX, 0, modZ);
    }

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
                        Ablockalypse.getExternal().getItemFileManager().giveItem(player, is);
                    }
                }, 10);
            }
        }, dropDelay);
    }

    public static String getLocationAsString(String prefix, Location loc) {
        return prefix + ": " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ", " + loc.getYaw() + ", " + loc.getPitch();
    }
    
    public static Block getHighestBlockUnder(Location loc) {
        for (int y = loc.getBlockY(); y > 0; y--) {
            Location floor = new Location(loc.getWorld(), loc.getX(), y, loc.getZ(), loc.getYaw(), loc.getPitch());
            Block block = floor.getBlock();
            if (!block.isEmpty()) {
                return block;
            }
        }
        return loc.getBlock();
    }
    
    public static Location floorLivingEntity(LivingEntity entity) {
        Location eyeLoc = entity.getEyeLocation().clone();
        double eyeHeight = entity.getEyeHeight();
        Location floor = eyeLoc.clone().subtract(0, Math.floor(eyeHeight) + .5, 0);
        for (int y = eyeLoc.getBlockY(); y > 0; y--) {
            Location loc = new Location(floor.getWorld(), floor.getX(), y, floor.getZ(), floor.getYaw(), floor.getPitch());
            if (!loc.getBlock().isEmpty()) {
                floor = loc;
                break;
            }
        }
        return eyeLoc.clone().subtract(0, eyeLoc.getY() - floor.getY() - (2 * eyeHeight), 0);
    }

    public static Block getSecondChest(Block b) {
        BlockFace[] faces = new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        for (BlockFace face : faces) {
            Block bl = b.getRelative(face);
            if (bl.getState() instanceof Chest || bl.getState() instanceof DoubleChest) {
                return bl;
            }
        }
        return null;
    }

    public static boolean isDoubleChest(Block block) {
        if (block == null) {
            return false;
        }
        if (!(block.getState() instanceof Chest)) {
            return false;
        }
        Chest chest = (Chest) block.getState();
        return chest.getInventory().getContents().length == 54;
    }

    public static boolean isEnchantableLikeSwords(ItemStack item) {
        for (int id : swords) {
            if (item.getTypeId() == id) {
                return true;
            }
        }
        return false;
    }

    public static boolean locationMatch(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }

    public static boolean locationMatch(Location loc1, Location loc2, int distance) {
        return loc1.distance(loc2) <= distance;
    }

    public static void randomPowerup(ZAPlayer zap, Entity cause) {
        int chance = rand.nextInt(100) + 1;
        if (chance <= (Integer) Setting.POWERUP_CHANCE.getSetting()) {
            zap.givePowerup(PowerupType.getById(rand.nextInt(5) + 1), cause);
        }
    }

    public static void setChestOpened(List<Player> players, Block block, boolean opened) {
        if ((block == null) || !(block.getState() instanceof Chest)) {
            return;
        }
        byte open = opened ? (byte) 1 : (byte) 0;
        for (Player player : players) {
            player.playNote(block.getLocation(), (byte) 1, open);
            if (isDoubleChest(block)) {
                player.playNote(getSecondChest(block).getLocation(), (byte) 1, open);
            }
        }
    }

    public static void setChestOpened(Player player, Block block, boolean opened) {
        if ((block == null) || !(block.getState() instanceof Chest)) {
            return;
        }
        byte open = opened ? (byte) 1 : (byte) 0;
        player.playNote(block.getLocation(), (byte) 1, open);
        if (isDoubleChest(block)) {
            player.playNote(getSecondChest(block).getLocation(), (byte) 1, open);
        }
    }
}
