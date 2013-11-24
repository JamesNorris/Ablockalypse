package com.github.jamesnorris.ablockalypse.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.github.jamesnorris.ablockalypse.aspect.intelligent.Path;

public class Pathfinder {
    private static List<Material> softBlocks = new ArrayList<Material>() {
        private static final long serialVersionUID = 4486955388377524651L;
        {
            add(Material.CARPET);
            add(Material.CROPS);
            add(Material.GRASS);
            add(Material.LEVER);
            add(Material.LONG_GRASS);
            add(Material.MELON_STEM);
            add(Material.NETHER_WARTS);
            add(Material.PAINTING);
            add(Material.POWERED_RAIL);
            add(Material.PUMPKIN_STEM);
            add(Material.RAILS);
            add(Material.RED_MUSHROOM);
            add(Material.RED_ROSE);
            add(Material.REDSTONE);
            add(Material.REDSTONE_COMPARATOR);
            add(Material.REDSTONE_WIRE);
            add(Material.SIGN);
            add(Material.SIGN_POST);
            add(Material.SNOW);
            add(Material.STATIONARY_LAVA);
            add(Material.STATIONARY_WATER);
            add(Material.STONE_PLATE);
            add(Material.STRING);
            add(Material.SUGAR_CANE_BLOCK);
            add(Material.TORCH);
            add(Material.TRIPWIRE);
            add(Material.VINE);
            add(Material.WALL_SIGN);
            add(Material.WATER);
            add(Material.WEB);
            add(Material.WHEAT);
            add(Material.WOOD_PLATE);
            add(Material.YELLOW_FLOWER);
        }
    };

    // TODO end the path where it cannot go any farther
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
        double totalHeuristic = 0;
        for (int n = 1; n <= 1000; n++) {
            double H = Double.MAX_VALUE;
            Location correct = null;
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Location check = current.clone().add(x, 0, z);
                    double newH = check.distanceSquared(target);
                    if (!check.getBlock().isEmpty() && /* check.getBlock().getType().isOccluding() */!softBlocks.contains(check.getBlock().getType())) {
                        if (check.clone().add(0, 1, 0).getBlock().isEmpty() || softBlocks.contains(check.clone().add(0, 1, 0).getBlock().getType())
                                && (check.clone().add(0, 2, 0).getBlock().isEmpty() || softBlocks.contains(check.clone().add(0, 2, 0).getBlock().getType()))) {
                            check = check.clone().add(0, 1, 0);
                            newH = check.distanceSquared(target);
                        } else {
                            newH += 400;// 20 squared
                        }
                    }
                    if (newH < H) {
                        H = newH;
                        correct = check;
                    }
                }
            }
            boolean newNode = correct != null && H < Double.MAX_VALUE && !BukkitUtility.locationMatch(correct, current);
            Location found = setLocationDirection(newNode ? correct : current, target);
            locations.put(n, getCoordinates(found));
            if (!newNode) {
                break;
            }
            totalHeuristic += H;
            current = correct;
            if (BukkitUtility.locationMatch(target, current, 2)) {
                break;// target reached
            }
        }
        return new Path(world, locations, totalHeuristic);
    }

    /**
     * Gets a serializable array of coordinates for this location.
     * 
     * [0] = x<br>
     * [1] = y<br>
     * [2] = z<br>
     * [3] = yaw<br>
     * [4] = pitch
     * 
     * @param found The location to get the coordinates array for
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
            if (node == null || !BukkitUtility.locationMatch(loc, node)) {
                continue;
            }
            return true;
        }
        return true;
    }

    public static boolean pathReaches(Path path, Location loc, int radiusDistance) {
        for (int i = 0; i <= path.getRawNodesMap().keySet().size(); i++) {
            Location node = path.getNode(i);
            if (node == null) {
                continue;
            }
            if (!BukkitUtility.locationMatch(loc, node, radiusDistance)) {
                return false;
            }
        }
        return true;
    }

    public static Location setLocationDirection(Location loc, Location lookat) {// TODO not working?
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
            loc.setYaw(loc.getYaw() - (float) Math.atan(dz / dx));
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
