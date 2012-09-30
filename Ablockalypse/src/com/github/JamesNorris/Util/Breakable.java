package com.github.JamesNorris.Util;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityWolf;
import net.minecraft.server.Packet40EntityMetadata;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.github.JamesNorris.Data.ByteData;

/**
 * The class for all breakable methods and code.
 */
public class Breakable {

	/**
	 * Gets the EntityPlayer from the NMS code for the specified player.
	 * 
	 * @param player The Player to get
	 * @return The NMS EntityPlayer
	 */
	public static EntityPlayer getNMSPlayer(Player player) {
		return ((CraftPlayer) player).getHandle();
	}

	/**
	 * Gets the Entity from the NMS code for the specified entity.
	 * 
	 * @param entity The entity to get
	 * @return The NMS entity
	 */
	public static net.minecraft.server.Entity getNMSEntity(Entity entity) {
		return ((CraftEntity) entity).getHandle();
	}

	/**
	 * Gets the EntityWolf from the NMS code for the specified wolf.
	 * 
	 * @param wolf The wolf to get
	 * @return The NMS EntityWolf
	 */
	public static EntityWolf getNMSWolf(Wolf wolf) {
		return ((CraftWolf) wolf).getHandle();
	}

	/**
	 * Gets the WorldServer from the NMS code for the specified world.
	 * 
	 * @param wolf The world to get
	 * @return The NMS WorldServer
	 */
	public static WorldServer getNMSWorld(World world) {
		return ((CraftWorld) world).getHandle();
	}

	/**
	 * Tells the specified entity to target the specified player.
	 * NOTE: Not meant for API use.
	 * 
	 * @param e The entity to do the targetting
	 * @param p The player to target
	 * @param loc The location of the entity spawn
	 * @param x The x coord of the entity
	 * @param z The z coord of the entity
	 * @param x2 The x coord of the player
	 * @param y2 The y coord of the player
	 * @param z2 The z coord of the player
	 */
	public static void targetPlayer(Entity e, Player p, Location loc, int x, int z, int x2, int y2, int z2) {
		EntityCreature ec = (EntityCreature) Breakable.getNMSEntity(e);
		ec.setTarget(Breakable.getNMSPlayer(p));
	}

	public static void setSitting(Player player, boolean tf) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			EntityPlayer ep = Breakable.getNMSPlayer(p);
			if (tf) {
				ep.netServerHandler.sendPacket(new Packet40EntityMetadata(player.getEntityId(), new ByteData((byte) 0x04)));
			} else {
				ep.netServerHandler.sendPacket(new Packet40EntityMetadata(player.getEntityId(), new ByteData((byte) 0x00)));
			}
		}
	}
}
