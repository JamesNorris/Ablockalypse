package com.github.Ablockalypse.JamesNorris.Util;

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
public final class SerializableLocation implements Serializable {
	private static final long serialVersionUID = 7661607750632441897L;

	/**
	 * Gets the org.bukkit.Location back from any SerializableLocation.
	 * 
	 * @param l The SerializableLocation to be returned as a Location
	 * @return An instance of Location
	 */
	public static Location returnLocation(final SerializableLocation l) {
		final float pitch = l.pitch;
		final float yaw = l.yaw;
		final double x = l.x;
		final double y = l.y;
		final double z = l.z;
		final World world = Bukkit.getWorld(l.world);
		final Location location = new Location(world, x, y, z, yaw, pitch);
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
	public SerializableLocation(final Location l) {
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
	public SerializableLocation(final Map<String, Object> map) {
		world = (String) map.get("world");
		uuid = (String) map.get("uuid");
		x = (Double) map.get("x");
		y = (Double) map.get("y");
		z = (Double) map.get("z");
		yaw = ((Float) map.get("yaw")).floatValue();
		pitch = ((Float) map.get("pitch")).floatValue();
	}

	/**
	 * Gets the SerializableLocation as an org.bukkit.Location for the defined server.
	 * 
	 * @param server The server to get the location from
	 * @return A Location from this SerializableLocation, that is located on the server
	 */
	public final Location getLocation(final Server server) {
		if (loc == null) {
			World world = server.getWorld(uuid);
			if (world == null) {
				world = server.getWorld(this.world);
			}
			loc = new Location(world, x, y, z, yaw, pitch);
		}
		return loc;
	}

	/**
	 * Returns a map of the SerializableLocation.
	 * 
	 * @return A map where the key is the type of argument, and the value is the argument
	 */
	public final Map<String, Object> serialize() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("world", world);
		map.put("uuid", uuid);
		map.put("x", x);
		map.put("y", y);
		map.put("z", z);
		map.put("yaw", yaw);
		map.put("pitch", pitch);
		return map;
	}
}
