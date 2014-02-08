package com.github.jamesnorris.ablockalypse.utility;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

/**
 * The class that allows location to be serialized, and be used
 * in a file. This class should not be changed, otherwise it
 * will not work for older files.
 * 
 * @author Jnorr44
 */
public final class SerialLocation implements Serializable {
    private static final long serialVersionUID = 2084461835352080670L;

    /**
     * Gets the org.bukkit.Location back from any SerializableLocation.
     * 
     * @param l The SerializableLocation to be returned as a Location
     * @return An instance of Location
     */
    public static Location returnLocation(SerialLocation l) {
        float pitch = l.pitch;
        float yaw = l.yaw;
        double x = l.x;
        double y = l.y;
        double z = l.z;
        World world = Bukkit.getWorld(l.world);
        Location location = new Location(world, x, y, z, yaw, pitch);
        return location;
    }

    private transient Location loc;
    private final String uuid;
    private final String world;
    private final double x, y, z;
    private final float yaw, pitch;

    /**
     * Creates a new SerializableLocation instance of any org.bukkit.Location.
     * 
     * @param l The location to be serialized
     */
    public SerialLocation(Location l) {
        world = l.getWorld().getName();
        uuid = l.getWorld().getUID().toString();
        x = l.getX();
        y = l.getY();
        z = l.getZ();
        yaw = l.getYaw();
        pitch = l.getPitch();
    }

    /**
     * Creates a new SerializableLocation instance of any org.bukkit.Location as a map.
     * 
     * @param map The map to be made into a SerializableLocation
     */
    public SerialLocation(Map<String, Object> map) {
        world = (String) map.get("world");
        uuid = (String) map.get("uuid");
        x = (Double) map.get("x");
        y = (Double) map.get("y");
        z = (Double) map.get("z");
        yaw = ((Float) map.get("yaw")).floatValue();
        pitch = ((Float) map.get("pitch")).floatValue();
    }

    public int getBlockX() {
        return (int) x;
    }

    public int getBlockY() {
        return (int) y;
    }

    public int getBlockZ() {
        return (int) z;
    }

    /**
     * Gets the SerializableLocation as an org.bukkit.Location for the defined server.
     * 
     * @param server The server to get the location from
     * @return A Location from this SerializableLocation, that is located on the server
     */
    public final Location getLocation(Server server) {
        if (loc == null) {
            World world = server.getWorld(uuid);
            if (world == null) {
                world = server.getWorld(this.world);
            }
            loc = new Location(world, x, y, z, yaw, pitch);
        }
        return loc;
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    /**
     * Returns a map of the SerializableLocation.
     * 
     * @return A map where the key is the type of argument, and the value is the argument
     */
    public final Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("world", world);
        map.put("uuid", uuid);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("yaw", yaw);
        map.put("pitch", pitch);
        return map;
    }

    @Override public String toString() {
        return "SerialLocation:(world: " + world + ", " + x + ", " + y + ", " + z + ", pitch: " + pitch + ", yaw: " + yaw + ")";
    }
}
