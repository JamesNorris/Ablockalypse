package com.github.jamesnorris.ablockalypse.utility;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Cuboid {
    private List<Location> border = new ArrayList<Location>();
    private List<Location> locations = new ArrayList<Location>();
    private Location corner1/* , corner2 */;
    private int maxX, maxY, maxZ, minX, minY, minZ;

    public Cuboid(Location corner1, Location corner2) {
        this.corner1 = corner1;
        /* this.corner2 = corner2; */
        maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(corner1.getWorld(), x, y, z, 0, 0);
                    if (x == maxX || y == maxY || z == maxZ || x == minX || y == minY || z == minZ) {
                        border.add(location);
                    }
                    locations.add(location);
                }
            }
        }
    }

    public List<Location> getBorder() {
        return border;
    }

    public Location getCorner(boolean highX, boolean highY, boolean highZ) {
        return new Location(corner1.getWorld(), highX ? maxX : minX, highY ? maxY : minY, highZ ? maxZ : minZ, 0, 0);
    }

    public List<Location> getLocations() {
        return locations;
    }
}
