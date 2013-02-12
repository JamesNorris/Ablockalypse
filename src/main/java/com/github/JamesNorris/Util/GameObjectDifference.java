package com.github.JamesNorris.Util;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class GameObjectDifference implements Serializable {
    private static final long serialVersionUID = -3122300037618945597L;
    public int x, y, z, x2 = 0, y2 = 0, z2 = 0, Xdif, Ydif, Zdif, Xdif2 = 0, Ydif2 = 0, Zdif2 = 0, typeid, typeid2;
    public String type, worldName;
    
    public GameObjectDifference(Location self, Location key, String type) {
        this.worldName = self.getWorld().getName();
        this.type = type;
        this.x = self.getBlockX();
        this.y = self.getBlockY();
        this.z = self.getBlockZ();
        this.Xdif = key.getBlockX() - x;
        this.Ydif = key.getBlockY() - y;
        this.Zdif = key.getBlockZ() - z;
        this.typeid = self.getBlock().getTypeId();
    }
    
    public void addSecondLocation(Location loc2, Location key) {
        this.x2 = loc2.getBlockX();
        this.y2 = loc2.getBlockY();
        this.z2 = loc2.getBlockZ();
        this.Xdif2 = key.getBlockX() - x2;
        this.Ydif2 = key.getBlockY() - y2;
        this.Zdif2 = key.getBlockZ() - z2;
        this.typeid2 = loc2.getBlock().getTypeId();
    }
    
    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);
        Location location = new Location(world, x, y, z, 0, 0);
        return location;
    }
}