package com.github.jamesnorris.util;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;

public class Pathfinder {
    private static double heuristic(double x1, double x2, double z1, double z2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((z1 - z1), 2));
    }

    private static double heuristic(Location check, Location target) {
        return heuristic(check.getX(), target.getX(), check.getZ(), target.getZ());
    }

    /**
     * Calculates and returns the path to the target from the starting point.
     * 
     * @param start The location to start the path at
     * @param target The location to find the path towards, starting at the start
     * @return The path from the start to the target
     */
    public static Path calculate(Location start, Location target) {
        HashMap<Integer, double[]> locations = new HashMap<Integer, double[]>();
        World world = start.getWorld();
        Location current = start;
        for (int n = 0; n < 99999; n++) {
            double[] coordinates = new double[3];
            double H = 99999D;
            Location correct = null;
            boolean horizontalSearch = true;
            verticalBlock: {
                for (int modY = 1; modY <= 15; modY++) {
                    Location clone = current.clone().subtract(0, modY, 0);
                    if (clone.getBlock().isEmpty()) {
                        H = heuristic(clone, target);
                        correct = clone;
                        horizontalSearch = false;
                        break verticalBlock;
                    }
                }
            }
            if (horizontalSearch) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        Location check = new Location(world, current.getBlockX() + x, current.getBlockY(), current.getBlockZ() + z);
                        if (!check.getBlock().isEmpty()) {
                            if (check.clone().add(0, 1, 0).getBlock().isEmpty() && check.clone().add(0, 2, 0).getBlock().isEmpty()) {
                                check = check.clone().add(0, 1, 0);
                            }
                        }
                        double newH = heuristic(check, target);
                        if (newH < H) {
                            H = newH;
                            correct = check;
                        }
                    }
                }
            }
            if (correct != null && H < 99999D && !MiscUtil.locationMatch(correct, current)) {
                coordinates[0] = correct.getX();
                coordinates[1] = correct.getY();
                coordinates[2] = correct.getZ();
                locations.put(n, coordinates);
                // System.out.println("target = Iteration# " + n + " X: " + target.getBlockX() + " Y: " + target.getBlockY() + " Z: " + target.getBlockZ());
                // System.out.println("move = Iteration# " + n + " X: " + correct.getBlockX() + " Y: " + correct.getBlockY() + " Z: " + correct.getBlockZ());
                current = correct;
            } else {
                coordinates[0] = current.getX();
                coordinates[1] = current.getY();
                coordinates[2] = current.getZ();
                locations.put(n, coordinates);
                break;// target cannot be reached
            }
            if (MiscUtil.locationMatch(target, current)) {
                break;// target reached
            }
        }
        return new Path(world, locations);
    }

    public static boolean pathReaches(Path path, Location loc) {
        for (int i = 0; i <= path.getRawNodesMap().keySet().size(); i++) {
            Location node = path.getNode(i);
            if (node == null)
                continue;
            if (!MiscUtil.locationMatch(loc, node))
                return false;
        }
        return true;
    }

    public static boolean pathReaches(Path path, Location loc, int radiusDistance) {
        for (int i = 0; i <= path.getRawNodesMap().keySet().size(); i++) {
            Location node = path.getNode(i);
            if (node == null)
                continue;
            if (!MiscUtil.locationMatch(loc, node, radiusDistance))
                return false;
        }
        return true;
    }
}
