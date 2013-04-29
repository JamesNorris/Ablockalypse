package com.github.jamesnorris.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Region {
    private Location loc1, loc2, loc3, loc4, loc5, loc6, loc7, loc8, center;
    private int lowX, highX, lowZ, highZ, lowY, highY, midX, midY, midZ;
    private World world;

    /**
     * Creates a new 3D region from 2 corner points.
     * @param start The first corner selected
     * @param end The second corner selected
     */
    public Region(Location start, Location end) {
        world = start.getWorld();
        int startX = start.getBlockX();
        int startY = start.getBlockY();
        int startZ = start.getBlockZ();
        int endX = end.getBlockX();
        int endY = end.getBlockY();
        int endZ = end.getBlockZ();
        highX = startX > endX ? startX : endX;
        lowX = startX <= endX ? startX : endX;
        highY = startY > endY ? startY : endY;
        lowY = startY <= endY ? startY : endY;
        highZ = startZ > endZ ? startZ : endZ;
        lowZ = startZ <= endZ ? startZ : endZ;
        midX = lowX + (highX - lowX) / 2;
        midY = lowY + (highY - lowY) / 2;
        midZ = lowZ + (highZ - lowZ) / 2;
        center = new Location(world, midX, midY, midZ);
        loc1 = world.getBlockAt(lowX, highY, highZ).getLocation();
        loc2 = world.getBlockAt(highX, highY, highZ).getLocation();
        loc3 = world.getBlockAt(highX, highY, lowZ).getLocation();
        loc4 = world.getBlockAt(lowX, highY, lowZ).getLocation();
        loc5 = world.getBlockAt(lowX, lowY, highZ).getLocation();
        loc6 = world.getBlockAt(highX, lowY, highZ).getLocation();
        loc7 = world.getBlockAt(highX, lowY, lowZ).getLocation();
        loc8 = world.getBlockAt(lowX, lowY, lowZ).getLocation();
    }

    /**
     * Checks if the location is contained inside the region.
     * @param loc The location to check for
     * @return Whether or not the location is contained in the region
     */
    public boolean contains(Location loc) {
        boolean Xs = overlap_1D(lowX, highX, loc.getBlockX(), loc.getBlockX());
        boolean Ys = overlap_1D(lowY, highY, loc.getBlockY(), loc.getBlockY());
        boolean Zs = overlap_1D(lowZ, highZ, loc.getBlockZ(), loc.getBlockZ());
        return Xs && Ys && Zs;
    }

    /**
     * Checks if the player is contained inside the region.
     * @param p The player to check for
     * @return Whether or not the player is contained in the region
     */
    public boolean contains(Player p) {
        return contains(p.getLocation());
    }

    /**
     * Gets the location at the very center of this region.
     * This may be slightly inaccurate as an estimate, since the location
     * is found using doubles, and block locations can only be integers.
     * Therefore, the getBlock() method for this center location may get
     * a block next to the middle, if the middle is between two or more blocks.
     * @return The location at (or close) to the center of the region
     */
    public Location getCenter() {
        return center;
    }

    /**
     * Gets the corner that matches the given number.
     * Corners are matched up to numbers in this order: <br>
     * 1. Low X, High Y, High Z <br>
     * 2. High X, High Y, High Z <br>
     * 3. High X, High Y, Low Z <br>
     * 4. Low X, High Y, Low Z <br>
     * 5. Low X, Low Y, High Z <br>
     * 6. High X, Low Y, High Z <br>
     * 7. High X, Low Y, Low Z <br>
     * 8. Low X, Low Y, Low Z
     * @param corner The corner number from 1-8
     * @return The corner corresponding to the number
     */
    public Location getCorner(int corner) {
        Location[] locs = new Location[] {loc1, loc2, loc3, loc4, loc5, loc6, loc7, loc8};
        return locs[corner];
    }

    /**
     * Gets the highest X of all 8 locations in this region.
     * @return The highest X value for this region
     */
    public int getHighestX() {
        return highX;
    }

    /**
     * Gets the highest Y of all 8 locations in this region.
     * @return The highest Y value for this region
     */
    public int getHighestY() {
        return highY;
    }

    /**
     * Gets the highest Z of all 8 locations in this region.
     * @return The highest Z value for this region
     */
    public int getHighestZ() {
        return highZ;
    }

    /**
     * Gets the lowest X of all 8 locations in this region.
     * @return The lowest X value for this region
     */
    public int getLowestX() {
        return lowX;
    }

    /**
     * Gets the lowest Y of all 8 locations in this region.
     * @return The lowest Y value for this region
     */
    public int getLowestY() {
        return lowY;
    }

    /**
     * Gets the lowest Z of all 8 locations in this region.
     * @return The lowest Z value for this region
     */
    public int getLowestZ() {
        return lowZ;
    }

    /**
     * Checks if the given region touches or overlaps this region.
     * @param other The region to check for
     * @return Whether or not they touch or overlap
     */
    public boolean overlaps(Region other) {
        boolean Xs = overlap_1D(lowX, highX, other.getLowestX(), other.getHighestX());
        boolean Ys = overlap_1D(lowY, highY, other.getLowestY(), other.getHighestY());
        boolean Zs = overlap_1D(lowZ, highZ, other.getLowestZ(), other.getHighestZ());
        return Xs && Ys && Zs;
    }

    /* Checks a 1D rectangle for overlap. */
    private boolean overlap_1D(double low1, double high1, double low2, double high2) {
        if (low1 <= low2) {
            return low2 <= high1;
        }
        return low1 <= high2;
    }
}
