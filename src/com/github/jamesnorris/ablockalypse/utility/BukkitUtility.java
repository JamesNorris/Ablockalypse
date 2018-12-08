package com.github.jamesnorris.ablockalypse.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BukkitUtility {
    public static List<Material> swords = new ArrayList<Material>() {{
        add(Material.WOODEN_SWORD);
        add(Material.STONE_SWORD);
        add(Material.IRON_SWORD);
        add(Material.GOLDEN_SWORD);
        add(Material.DIAMOND_SWORD);
    }};

    public static HashMap<DyeColor, Material> woolByColor;

    private static Random rand = new Random();
    private static String nms_version = "v1.5.2";
    static {
        String bukkitVersion = Bukkit.getVersion();
        String cleanedVersion = bukkitVersion.split(Pattern.quote("(MC:"))[1].split(Pattern.quote(")"))[0].trim();
        nms_version = "v" + cleanedVersion;

        woolByColor = new HashMap<DyeColor, Material>()
        woolByColor.put(DyeColor.BLACK, Material.BLACK_WOOL);
        woolByColor.put(DyeColor.BLUE, Material.BLUE_WOOL);
        woolByColor.put(DyeColor.BROWN, Material.BROWN_WOOL);
        woolByColor.put(DyeColor.CYAN, Material.CYAN_WOOL);
        woolByColor.put(DyeColor.GRAY, Material.GRAY_WOOL);
        woolByColor.put(DyeColor.GREEN, Material.GREEN_WOOL);
        woolByColor.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_WOOL);
        woolByColor.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_WOOL);
        woolByColor.put(DyeColor.LIME, Material.LIME_WOOL);
        woolByColor.put(DyeColor.MAGENTA, Material.MAGENTA_WOOL);
        woolByColor.put(DyeColor.ORANGE, Material.ORANGE_WOOL);
        woolByColor.put(DyeColor.PINK, Material.PINK_WOOL);
        woolByColor.put(DyeColor.PURPLE, Material.PURPLE_WOOL);
        woolByColor.put(DyeColor.RED, Material.RED_WOOL);
        woolByColor.put(DyeColor.WHITE, Material.WHITE_WOOL);
        woolByColor.put(DyeColor.YELLOW, Material.YELLOW_WOOL);
    }

    public static Material getWoolByColor(DyeColor color) {
        return woolByColor.get(color);
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

    public static OfflinePlayer forceObtainPlayer(String name) {
        OfflinePlayer player = Bukkit.getPlayer(name);
        if (player == null) {
            return Bukkit.getOfflinePlayer(name);
        }
        if (player == null || !player.hasPlayedBefore()) {
            // npes will be thrown... player doesnt exist and never did (why was it saved?)
            return null;
        }
        return player;
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

    public static List<Entity> getNearbyEntities(Location loc, double x, double y, double z) {
        List<Entity> entities = new ArrayList<Entity>();
        for (Chunk chunk : getRelativeChunks(loc.getChunk())) {
            for (Entity entity : chunk.getEntities()) {
                Location entLoc = entity.getLocation();
                if (Math.abs(entLoc.getX() - loc.getX()) <= x && Math.abs(entLoc.getY() - loc.getY()) <= y && Math.abs(entLoc.getZ() - loc.getZ()) <= z && !entities.contains(entity)) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

    public static Location getNearbyLocation(Location loc, double minXdif, double maxXdif, double minYdif, double maxYdif, double minZdif, double maxZdif) {
        double modX = difInRandDirection(maxXdif, minXdif);
        double modY = difInRandDirection(maxXdif, minXdif);
        double modZ = difInRandDirection(maxXdif, minXdif);
        return loc.clone().add(modX, modY, modZ);
    }

    public static String getNMSVersionSlug() {
        return nms_version;
    }

    /**
     * Gets all nearby chunks
     * @param chunk The chunk to find relatives
     * @return All nearby chunks, including the one passed as a parameter
     */
    public static Chunk[] getRelativeChunks(Chunk chunk) {
        World world = chunk.getWorld();
        return new Chunk[] {
            chunk, //TODO check current chunk?
            world.getChunkAt(chunk.getX() - 16, chunk.getZ() - 16),
            world.getChunkAt(chunk.getX() - 16, chunk.getZ()),
            world.getChunkAt(chunk.getX(), chunk.getZ() - 16),
            world.getChunkAt(chunk.getX(), chunk.getZ()), 
            world.getChunkAt(chunk.getX() + 16, chunk.getZ() - 16),
            world.getChunkAt(chunk.getX() - 16, chunk.getZ() + 16),
            world.getChunkAt(chunk.getX() + 16, chunk.getZ()), 
            world.getChunkAt(chunk.getX(), chunk.getZ() + 16)
        };
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
        return swords.Contains(item.getType());
    }

    public static boolean locationMatch(Location loc1, Location loc2) {
        boolean nearX = Math.floor(loc1.getBlockX()) == Math.floor(loc2.getBlockX());
        boolean nearY = Math.floor(loc1.getBlockY()) == Math.floor(loc2.getBlockY());
        boolean nearZ = Math.floor(loc1.getBlockZ()) == Math.floor(loc2.getBlockZ());
        return nearX && nearY && nearZ;
    }

    public static boolean locationMatch(Location loc1, Location loc2, int distance) {// TODO this method is exact, fix
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

    private static double difInRandDirection(double max, double min) {
        return (rand.nextBoolean() ? 1 : -1) * (rand.nextDouble() * Math.abs(max - min) + Math.abs(min));
    }
}
