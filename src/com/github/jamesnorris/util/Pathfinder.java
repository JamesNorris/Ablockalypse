package com.github.jamesnorris.util;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;

public class Pathfinder {
    /**
     * Calculates and returns the path to the target from the starting point.
     * This also accounts for pitch and yaw toward the target.
     * 
     * @param start The location to start the path at
     * @param target The location to find the path towards, starting at the start
     * @return The path from the start to the target
     */
    public static Path calculate(Location start, Location target) {
        HashMap<Integer, double[]> locations = new HashMap<Integer, double[]>();
        World world = start.getWorld();
        Location current = start.subtract(0, 1, 0);
        locations.put(0, getCoordinates(setLocationDirection(current, target)));
        for (int n = 1; n <= 1000; n++) {
            double H = 99999D;
            Location correct = null;
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Location check = new Location(world, current.getX() + x, current.getY(), current.getZ() + z);
                    double newH = check.distance(target);
                    if (!check.getBlock().isEmpty()) {
                        if (check.clone().add(0, 1, 0).getBlock().isEmpty() && check.clone().add(0, 2, 0).getBlock().isEmpty()) {
                            check = check.clone().add(0, 1, 0);
                            newH = check.distance(target);
                        } else {
                            newH += 20;
                        }
                    }
                    if (newH < H) {
                        H = newH;
                        correct = check;
                    }
                }
            }
            boolean newNode = correct != null && H < 99999D && !MiscUtil.locationMatch(correct, current);
            Location found = setLocationDirection(newNode ? correct : current, target);
            locations.put(n, getCoordinates(found));
            if (!newNode) {
                break;
            }
            current = correct;
            if (MiscUtil.locationMatch(target, current, 2)) {
                break;// target reached
            }
        }
        return new Path(world, locations);
    }

    /**
     * [0] = x
     * [1] = y
     * [2] = z
     * [3] = yaw
     * [4] = pitch
     */
    public static double[] getCoordinates(Location found) {
        double[] coordinates = new double[5];
        coordinates[0] = found.getX();
        coordinates[1] = found.getY();
        coordinates[2] = found.getZ();
        coordinates[3] = found.getYaw();
        coordinates[4] = found.getPitch();
        return coordinates;
    }

    public static boolean pathReaches(Path path, Location loc) {
        for (int i = 0; i <= path.getRawNodesMap().keySet().size(); i++) {
            Location node = path.getNode(i);
            if (node == null) {
                continue;
            }
            if (!MiscUtil.locationMatch(loc, node)) {
                return false;
            }
        }
        return true;
    }

    public static boolean pathReaches(Path path, Location loc, int radiusDistance) {
        for (int i = 0; i <= path.getRawNodesMap().keySet().size(); i++) {
            Location node = path.getNode(i);
            if (node == null) {
                continue;
            }
            if (!MiscUtil.locationMatch(loc, node, radiusDistance)) {
                return false;
            }
        }
        return true;
    }

    public static Location setLocationDirection(Location loc, Location lookat) {
        loc = loc.clone();
        // double b = lookat.getX() - loc.getX();
        // double d = lookat.getY() - loc.getY();
        // double a = lookat.getZ() - loc.getZ();
        // double c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
        // double e = Math.sqrt(Math.pow(c, 2) + Math.pow(d, 2));
        // loc.setYaw((float) Math.toDegrees(Math.asin(a / c)));
        // loc.setPitch((float) Math.toDegrees(Math.asin(d / e)));
        // or... -----------------------------------------------------
        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();
        if (dx != 0) {
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
        loc.setPitch((float) -Math.atan(dy / dxz));
        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);
        return loc;
    }
}
