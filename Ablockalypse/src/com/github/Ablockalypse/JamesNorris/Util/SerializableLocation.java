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
	public static Location returnLocation(SerializableLocation l) {
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
	public SerializableLocation(Location l) {
		this.world = l.getWorld().getName();
		this.uuid = l.getWorld().getUID().toString();
		this.x = l.getX();
		this.y = l.getY();
		this.z = l.getZ();
		this.yaw = l.getYaw();
		this.pitch = l.getPitch();
	}

	/**
	 * Creates a new SerializableLocation instance of any org.bukkit.Location as a map.
	 * 
	 * @param map The map to be made into a SerializableLocation
	 */
	public SerializableLocation(Map<String, Object> map) {
		this.world = (String) map.get("world");
		this.uuid = (String) map.get("uuid");
		this.x = (Double) map.get("x");
		this.y = (Double) map.get("y");
		this.z = (Double) map.get("z");
		this.yaw = ((Float) map.get("yaw")).floatValue();
		this.pitch = ((Float) map.get("pitch")).floatValue();
	}

	/**
	 * Gets the SerializableLocation as an org.bukkit.Location for the defined server.
	 * 
	 * @param server The server to get the location from
	 * @return A Location from this SerializableLocation, that is located on the server
	 */
	public final Location getLocation(Server server) {
		if (loc == null) {
			World world = server.getWorld(this.uuid);
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
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("world", this.world);
		map.put("uuid", this.uuid);
		map.put("x", this.x);
		map.put("y", this.y);
		map.put("z", this.z);
		map.put("yaw", this.yaw);
		map.put("pitch", this.pitch);
		return map;
	}
}
