package com.github.jamesnorris.ablockalypse.aspect.intelligent;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;

public class Path {
    private HashMap<Integer, double[]> locations = new HashMap<Integer, double[]>();
    private World world;
    private double totalHeuristic = -1;

    public Path(World world, HashMap<Integer, double[]> locations) {
        this(world, locations, -1);
    }

    public Path(World world, HashMap<Integer, double[]> locations, double totalHeuristic) {
        this.world = world;
        this.locations = locations;
        this.totalHeuristic = totalHeuristic;
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

    /**
     * Gets the location related to the number given.
     * The numbers are in sequence towards the target.
     * 
     * @param nodeNumber The number of the point to get
     * @return The location related to the number given
     */
    public Location getNode(int nodeNumber) {
        if (locations.get(nodeNumber) != null) {
            double[] coords = locations.get(nodeNumber);
            return new Location(world, coords[0], coords[1], coords[2], (float) coords[3], (float) coords[4]);
        }
        return null;
    }

    public int getNodeAmount() {
        return locations.size();
    }

    public double getPitch(int nodeNumber) {
        return locations.get(nodeNumber)[4];
    }

    public HashMap<Integer, double[]> getRawNodesMap() {
        return locations;
    }

    // can return -1 if no heuristic arg is provided in the constructor
    @Deprecated public double getTotalHeuristic() {
        return totalHeuristic;
    }

    public double getX(int nodeNumber) {
        return locations.get(nodeNumber)[0];
    }

    public double getY(int nodeNumber) {
        return locations.get(nodeNumber)[1];
    }

    public double getYaw(int nodeNumber) {
        return locations.get(nodeNumber)[3];
    }

    public double getZ(int nodeNumber) {
        return locations.get(nodeNumber)[2];
    }
}
