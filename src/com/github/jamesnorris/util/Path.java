package com.github.jamesnorris.util;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;

public class Path {
    private World world;
    private HashMap<Integer, double[]> locations = new HashMap<Integer, double[]>();

    public Path(World world, HashMap<Integer, double[]> locations) {
        this.world = world;
        this.locations = locations;
    }

    public HashMap<Integer, double[]> getRawNodesMap() {
        return locations;
    }

    /**
     * Gets the location related to the number given.
     * The numbers are in sequence towards the target.
     * 
     * @param nodeNumber The number of the point to get
     * @return The location related to the number given
     */
    public Location getNode(int nodeNumber) {
        if (!(locations.get(nodeNumber) == null)) {
            double[] coords = locations.get(nodeNumber);
            return new Location(world, coords[0], coords[1], coords[2]);
        }
        return null;
    }

    public Location getEndNode() {
        for (int i = locations.size(); i > 0; i--) {
            Location node = getNode(i);
            if (node != null) {
                return node;
            }
        }
        return null;
    }
    
    public double getX(int nodeNumber) {
        return locations.get(nodeNumber)[0];
    }
    
    public double getY(int nodeNumber) {
        return locations.get(nodeNumber)[1];
    }
    
    public double getZ(int nodeNumber) {
        return locations.get(nodeNumber)[2];
    }
}
