package com.github.jamesnorris.util;

import java.util.ArrayList;

//@formatter:off
//BREAKABLE IMPORTS
import net.minecraft.server.v1_5_R2.DataWatcher;
import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.Packet40EntityMetadata;
import net.minecraft.server.v1_5_R2.WatchableObject;

import org.bukkit.craftbukkit.v1_5_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
//@formatter:on

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.enumerated.Local;

public class Breakable {
    
    public class ByteData extends DataWatcher {
        private byte data;

        private ByteData(byte data) {
            this.data = data;
        }

        @Override public ArrayList<WatchableObject> b() {
            ArrayList<WatchableObject> list = new ArrayList<WatchableObject>();
            list.add(new WatchableObject(0, 0, data));
            return list;
        }
    }

    /**
     * Gets the Entity from the NMS code for the specified entity.
     * 
     * @param entity The entity to get
     * @return The NMS entity
     */
    public static net.minecraft.server.v1_5_R2.Entity getNMSEntity(Entity entity) {
        try {
            return ((CraftEntity) entity).getHandle();
        } catch (NoClassDefFoundError e) {
            Ablockalypse.crash(Local.WRONG_VERSION.getSetting(), true);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the EntityPlayer from the NMS code for the specified player.
     * 
     * @param player The Player to get
     * @return The NMS EntityPlayer
     */
    public static EntityPlayer getNMSPlayer(Player player) {
        try {
            return ((CraftPlayer) player).getHandle();
        } catch (NoClassDefFoundError e) {
            Ablockalypse.crash(Local.WRONG_VERSION.getSetting(), true);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Makes the player appear to be sitting down/standing up.
     * 
     * @param player The player to change
     * @param tf Whether or not to make the player stand up or sit down
     */
    public static void setSitting(Player player, boolean tf) {
        try {
            byte b1 = (tf) ? (byte) 0x04 : (byte) 0x00;
            for (Player p : Bukkit.getOnlinePlayers()) {
                EntityPlayer ep = Breakable.getNMSPlayer(p);
                ep.playerConnection.sendPacket(new Packet40EntityMetadata(player.getEntityId(), new Breakable().new ByteData(b1), true));
            }
            double modY = (tf) ? -.5 : .5;
            player.teleport(player.getLocation().add(0, modY, 0));
        } catch (NoClassDefFoundError e) {
            Ablockalypse.crash(Local.WRONG_VERSION.getSetting(), true);
            e.printStackTrace();
        }
    }
}
