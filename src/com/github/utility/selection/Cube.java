package com.github.utility.selection;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Cube {
    private Location loc;
    private ArrayList<Location> locs;
    private World world;
    private int x, y, z, radius, i, j, k;

    /**
     * Creates a new cube, with the center being the given location,
     * and the radius being the given radius.
     * 
     * @param centerLocation The center of the cube
     * @param radius The radius of the cube
     */
    public Cube(Location centerLocation, int radius) {
        x = centerLocation.getBlockX();
        y = centerLocation.getBlockY();
        z = centerLocation.getBlockZ();
        locs = new ArrayList<Location>();
        this.radius = radius;
        world = centerLocation.getWorld();
        for (i = -radius; i <= radius; i++) {
            for (j = -radius; j <= radius; j++) {
                for (k = -radius; k <= radius; k++) {
                    loc = world.getBlockAt(x + i, y + j, z + k).getLocation();
                    locs.add(loc);
                }
            }
        }
    }

    /**
     * Changes the type of all blocks in a cube from the specified fromMaterial,
     * to the specified toMaterial.
     * 
     * @param fromMaterial The type to change from
     * @param toMaterial The type to change to
     */
    public void changeFromToType(Material fromMaterial, Material toMaterial) {
        for (Location l : locs) {
            Block b = l.getBlock();
            if (b.getType() == fromMaterial) {
                b.setType(toMaterial);
            }
        }
    }

    /**
     * Changes the type of all blocks in the cube to the specified material.
     * 
     * @param material The type to change the blocks to
     */
    public void changeType(Material material) {
        for (Location l : locs) {
            l.getBlock().setType(material);
        }
    }

    /**
     * Checks if the cube contains the defined material.
     * 
     * @param material The type to check for
     * @return Whether or not the material is in the cube
     */
    public boolean contains(Material material) {
        for (Location l : locs) {
            if (l.getBlock().getType() == material) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all locations within the cube.
     * 
     * @return A list of locations within the cube
     */
    public ArrayList<Location> getLocations() {
        return locs;
    }

    /**
     * Gets the radius of the cube.
     * 
     * @return The radius of the cube
     */
    public int getRadius() {
        return radius;
    }
}
