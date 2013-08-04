package com.github.utility.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Rectangle {
    private ArrayList<Location> border2D = new ArrayList<Location>();
    private ArrayList<Location> border3D = new ArrayList<Location>();
    private Location loc1, loc2, loc3, loc4;
    private HashMap<Location, Material> locs = new HashMap<Location, Material>();

    /**
     * Creates a new rectangle from one location to the next.
     * 
     * @param loc1 The first location
     * @param loc2 The second location
     */
    public Rectangle(Location loc1, Location loc2) {
        World world = loc1.getWorld();
        this.loc1 = loc1;
        this.loc2 = loc2;
        loc3 = world.getBlockAt(loc2.getBlockX(), 0, loc1.getBlockZ()).getLocation();
        loc4 = world.getBlockAt(loc1.getBlockX(), 0, loc2.getBlockZ()).getLocation();
        calculate();
    }

    /**
     * Gets the 2D border of blocks around the rectangle.
     * 
     * @return The rectangle's 2D border
     */
    public ArrayList<Location> get2DBorder() {
        return border2D;
    }

    /**
     * @deprecated The 3D border is not accurate
     * 
     * Gets the 3D border of blocks around the rectangle.
     * 
     * @return The rectangle's 3D border
     */
    @Deprecated public ArrayList<Location> get3DBorder() {
        return border3D;
    }

    public Location getCorner(int corner) {
        Location[] corners = new Location[] {loc1, loc2, loc3, loc4};
        return corners[corner - 1];
    }

    /**
     * Gets all the locations from the rectangle in a list.
     * 
     * @return A list of all locations in the rectangle
     */
    public List<Location> getLocations() {
        ArrayList<Location> locations = new ArrayList<Location>();
        for (Location l : locs.keySet()) {
            locations.add(l);
        }
        return locations;
    }

    /* Calculates a rectangle from loc1 to loc2. */
    private void calculate() {
        locs.clear();
        int x = loc1.getBlockX(), y = loc1.getBlockY(), z = loc1.getBlockZ();
        int x2 = loc2.getBlockX(), y2 = loc2.getBlockY(), z2 = loc2.getBlockZ();
        int x3 = loc3.getBlockX(), y3 = loc3.getBlockY(), z3 = loc3.getBlockZ();
        int x4 = loc4.getBlockX(), y4 = loc4.getBlockY(), z4 = loc4.getBlockZ();
        int lowX = x < x2 ? x : x2;
        int highX = x > x2 ? x : x2;
        int lowY = y < y2 ? y : y2;
        int highY = y > y2 ? y : y2;
        int lowZ = z < z2 ? z : z2;
        int highZ = z > z2 ? z : z2;
        for (int i = lowX; i <= highX; i++) {
            for (int j = lowY; j <= highY; j++) {
                for (int k = lowZ; k <= highZ; k++) {
                    Location l = loc1.getWorld().getBlockAt(i, j, k).getLocation();
                    locs.put(l, l.getBlock().getType());
                }
            }
        }
        // border
        // X
        for (int i = lowX; i <= highX; i++) {
            Location l = loc1.getWorld().getBlockAt(i, y, z).getLocation();
            Location l22D = loc1.getWorld().getBlockAt(i, y, z2).getLocation();
            Location l2 = loc1.getWorld().getBlockAt(i, y2, z2).getLocation();
            Location l3 = loc1.getWorld().getBlockAt(i, y3, z3).getLocation();
            Location l4 = loc1.getWorld().getBlockAt(i, y4, z4).getLocation();
            if (!border3D.contains(l)) {
                border3D.add(l);
            }
            if (!border3D.contains(l2)) {
                border3D.add(l2);
            }
            if (!border3D.contains(l3)) {
                border3D.add(l3);
            }
            if (!border3D.contains(l4)) {
                border3D.add(l4);
            }
            if (!border2D.contains(l)) {
                border2D.add(l);
            }
            if (!border2D.contains(l22D)) {
                border2D.add(l22D);
            }
        }
        // Y
        for (int j = lowY; j <= highY; j++) {
            Location l = loc1.getWorld().getBlockAt(x, j, z).getLocation();
            Location l2 = loc1.getWorld().getBlockAt(x2, j, z2).getLocation();
            Location l3 = loc1.getWorld().getBlockAt(x3, j, z3).getLocation();
            Location l4 = loc1.getWorld().getBlockAt(x4, j, z4).getLocation();
            if (!border3D.contains(l)) {
                border3D.add(l);
            }
            if (!border3D.contains(l2)) {
                border3D.add(l2);
            }
            if (!border3D.contains(l3)) {
                border3D.add(l3);
            }
            if (!border3D.contains(l4)) {
                border3D.add(l4);
            }
        }
        // Z
        for (int k = lowZ; k <= highZ; k++) {
            Location l = loc1.getWorld().getBlockAt(x, y, k).getLocation();
            Location l22D = loc1.getWorld().getBlockAt(x, y, k).getLocation();
            Location l2 = loc1.getWorld().getBlockAt(x2, y2, k).getLocation();
            Location l3 = loc1.getWorld().getBlockAt(x3, y3, k).getLocation();
            Location l4 = loc1.getWorld().getBlockAt(x4, y4, k).getLocation();
            if (!border3D.contains(l)) {
                border3D.add(l);
            }
            if (!border3D.contains(l2)) {
                border3D.add(l2);
            }
            if (!border3D.contains(l3)) {
                border3D.add(l3);
            }
            if (!border3D.contains(l4)) {
                border3D.add(l4);
            }
            if (!border2D.contains(l)) {
                border2D.add(l);
            }
            if (!border2D.contains(l22D)) {
                border2D.add(l22D);
            }
        }
    }
}
