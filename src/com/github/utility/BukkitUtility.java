package com.github.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
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
import com.github.threading.DelayedTask;

public class BukkitUtility {
    public static int[] swords = new int[] {268, 283, 272, 267, 276};
    private static Random rand = new Random();
    private static String nms_version = "v1.5.2";
    static {
        String bukkitVersion = Bukkit.getVersion();
        String cleanedVersion = bukkitVersion.split(Pattern.quote("(MC:"))[1].split(Pattern.quote(")"))[0].trim();
        nms_version = "v" + cleanedVersion;
    }
    
    public static Location fromString(String loc) {
        loc = loc.substring(loc.indexOf("{") + 1);
        loc = loc.substring(loc.indexOf("{") + 1);
        String worldName = loc.substring(loc.indexOf("=") + 1, loc.indexOf("}"));
        loc = loc.substring(loc.indexOf(",") + 1);
        String xCoord = loc.substring(loc.indexOf("=") + 1, loc.indexOf(","));
        loc = loc.substring(loc.indexOf(",") + 1);
        String yCoord = loc.substring(loc.indexOf("=") + 1, loc.indexOf(","));
        loc = loc.substring(loc.indexOf(",") + 1);
        String zCoord = loc.substring(loc.indexOf("=") + 1, loc.indexOf(","));
        loc = loc.substring(loc.indexOf(",") + 1);
        String pitch = loc.substring(loc.indexOf("=") + 1, loc.indexOf(","));
        loc = loc.substring(loc.indexOf(",") + 1);
        String yaw = loc.substring(loc.indexOf("=") + 1, loc.indexOf("}"));
        return new Location(Bukkit.getWorld(worldName), Double.parseDouble(xCoord), Double.parseDouble(yCoord), Double.parseDouble(zCoord), Float.parseFloat(yaw), Float.parseFloat(pitch));
    }
    
    public static List<Entity> getNearbyEntities(Location loc, double x, double y, double z) {
        List<Entity> entities = new ArrayList<Entity>();
        for (Entity entity : loc.getWorld().getEntities()) {
            Location entLoc = entity.getLocation();
            boolean nearX = Math.abs(entLoc.getX() - loc.getX()) <= x;
            boolean nearY = Math.abs(entLoc.getY() - loc.getY()) <= y;
            boolean nearZ = Math.abs(entLoc.getZ() - loc.getZ()) <= z;
            if (nearX && nearY && nearZ) {
                entities.add(entity);
            }
        }
        return entities;
    }

    public static void dropItemAtPlayer(final Location from, final ItemStack item, final Player player, final int dropDelay, final int removalDelay) {
        new DelayedTask(dropDelay, true) {
            @Override public void run() {
                Item i = from.getWorld().dropItem(from, item);
                i.setPickupDelay(Integer.MAX_VALUE);
                final ItemStack is = i.getItemStack();
                final Item finali = i;
                new DelayedTask(removalDelay, true) {
                    @Override public void run() {
                        finali.remove();
                        Ablockalypse.getExternal().getItemFileManager().giveItem(player, is);
                    }
                };
            }
        };
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
        return eyeLoc.clone().subtract(0, eyeLoc.getY() - floor.getY() - 2 * eyeHeight, 0);
    }

    public static Block getHighestEmptyBlockUnder(Location loc) {
        for (int y = loc.getBlockY(); y > 0; y--) {
            Location floor = new Location(loc.getWorld(), loc.getX(), y, loc.getZ(), loc.getYaw(), loc.getPitch());
            Block block = floor.getBlock();
            if (!block.isEmpty()) {
                return block;
            }
        }
        return loc.getBlock();
    }

    public static Location getNearbyLocation(Location loc, int minXdif, int maxXdif, int minYdif, int maxYdif, int minZdif, int maxZdif) {
        int modX = difInRandDirection(maxXdif, minXdif);
        int modY = difInRandDirection(maxXdif, minXdif);
        int modZ = difInRandDirection(maxXdif, minXdif);
        return loc.clone().add(modX, modY, modZ);
    }

    private static int difInRandDirection(int max, int min) {
        try {
            return (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(Math.abs(max - min)) + min);
        } catch (IllegalArgumentException e) {
            // nothing, the number to change by is 0, throwing the exception because on rand.nextInt(n <= 0).
        }
        return 0;
    }

    public static String getNMSVersionSlug() {
        return nms_version;
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
        if (block == null || !(block.getState() instanceof Chest)) {
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
        boolean nearX = Math.floor(loc1.getBlockX()) == Math.floor(loc2.getBlockX());
        boolean nearY = Math.floor(loc1.getBlockY()) == Math.floor(loc2.getBlockY());
        boolean nearZ = Math.floor(loc1.getBlockZ()) == Math.floor(loc2.getBlockZ());
        return nearX && nearY && nearZ;
    }

    public static boolean locationMatch(Location loc1, Location loc2, int distance) {//TODO this method is exact, fix
        return Math.abs(loc1.getX() - loc2.getX()) <= distance && Math.abs(loc1.getY() - loc2.getY()) <= distance && Math.abs(loc1.getZ() - loc2.getZ()) <= distance;
    }
    
    public static boolean locationMatchExact(Location loc1, Location loc2) {
        return locationMatchExact(loc1, loc2, 0);
    }
    
    public static boolean locationMatchExact(Location loc1, Location loc2, double distance) {
        return loc1.distanceSquared(loc2) <= Math.pow(distance, 2);
    }

    public static void setChestOpened(List<Player> players, Block block, boolean opened) {
        if (block == null || !(block.getState() instanceof Chest)) {
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
        if (block == null || !(block.getState() instanceof Chest)) {
            return;
        }
        byte open = opened ? (byte) 1 : (byte) 0;
        player.playNote(block.getLocation(), (byte) 1, open);
        if (isDoubleChest(block)) {
            player.playNote(getSecondChest(block).getLocation(), (byte) 1, open);
        }
    }
}
