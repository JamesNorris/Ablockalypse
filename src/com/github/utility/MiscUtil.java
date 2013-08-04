package com.github.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.behavior.ZAScheduledTask;

public class MiscUtil {
    public static int[] swords = new int[] {268, 283, 272, 267, 276};
    private static Random rand = new Random();
    private static String nms_version = "v1.5.2";
    static {
        String bukkitVersion = Bukkit.getVersion();
        String cleanedVersion = bukkitVersion.split(Pattern.quote("(MC:"))[1].split(Pattern.quote(")"))[0].trim();
        nms_version = "v" + cleanedVersion;
    }

    public static void dropItemAtPlayer(final Location from, final ItemStack item, final Player player, final int dropDelay, final int removalDelay) {
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
                }, removalDelay);
            }
        }, dropDelay);
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

    public static Field getField(Class<?> cl, String fieldName) {
        for (Field f : cl.getFields()) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
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

    public static String getLastMethodCalls(Thread thread, int number) {
        StackTraceElement[] stackTraceElements = thread.getStackTrace();
        StringBuilder sb = new StringBuilder();
        int start = 2;
        for (int i = start; i <= number + start - 1; i++) {
            sb.append(stackTraceElements[i] + "\n");
        }
        return sb.toString();
    }

    public static Method getMethod(Class<?> cl, String methodName) {
        for (Method m : cl.getMethods()) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        return null;
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

    public static <T> T getObject(Class<T> type, Object cast) {
        if (type.isInstance(cast)) {
            return type.cast(cast);
        } else {
            return null;
        }
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
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }

    public static boolean locationMatch(Location loc1, Location loc2, int distance) {
        return loc1.distanceSquared(loc2) <= Math.pow(distance, 2);
    }

    public static Integer[] parseIntervalNotation(String line) {
        List<Integer> listLevels = new ArrayList<Integer>();
        int includingStart = line.indexOf("[");
        int notIncludingStart = line.indexOf("(");
        int includingEnd = line.indexOf("]");
        int notIncludingEnd = line.indexOf(")");
        boolean startIncludes = includingStart != -1;
        boolean endIncludes = includingEnd != -1;
        String[] integers = line.substring((startIncludes ? includingStart : notIncludingStart) + 1, endIncludes ? includingEnd : notIncludingEnd).split(Pattern.quote(","));
        int start = Integer.parseInt(integers[0].trim());
        int end = Integer.parseInt(integers[1].trim());
        for (int i = start; i <= end; i++) {
            listLevels.add(i);
        }
        return listLevels.toArray(new Integer[listLevels.size()]);
    }

    public static int parsePercentage(String line) {
        HashMap<Integer, Integer> digits = new HashMap<Integer, Integer>();
        int signIndex = line.indexOf("%");
        for (int i = 1; i <= 3; i++) {
            char beforeSign = line.charAt(signIndex - i);
            if (!Character.isDigit(beforeSign)) {
                break;
            }
            digits.put((int) beforeSign, i - 1);
        }
        int percent = 0;
        for (Integer digit : digits.keySet()) {
            int zeros = digits.get(digit);
            percent += digit * Math.pow(10, zeros);
        }
        return percent;
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
