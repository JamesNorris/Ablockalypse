package com.github.utility.ranged;

import java.util.UUID;

import org.bukkit.Location;

public class DataZone {
    private final UUID uuid = UUID.randomUUID();
    
    public UUID getUUID() {
        return uuid;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getThickness() {
        return thickness;
    }

    public void setThickness(double thickness) {
        this.thickness = thickness;
    }

    public double widthFrom, widthTo, heightFrom, heightTo, lengthFrom, lengthTo;
    private double multiplier, thickness;
    
    public DataZone(Location lowest, Location highest) {
        this(lowest.getX(), highest.getX(), lowest.getY(), highest.getY(), lowest.getZ(), highest.getZ(), 1, 1);
    }
    
    public DataZone(Location lowest, Location highest, double multiplier, double thickness) {
        this(lowest.getX(), highest.getX(), lowest.getY(), highest.getY(), lowest.getZ(), highest.getZ(), multiplier, thickness);
    }
    
    public DataZone(double widthFrom, double widthTo, double heightFrom, double heightTo, double lengthFrom, double lengthTo) {
        this(widthFrom, widthTo, heightFrom, heightTo, lengthFrom, lengthTo, 1, 1);
    }
    
    public DataZone(double widthFrom, double widthTo, double heightFrom, double heightTo, double lengthFrom, double lengthTo, double multiplier, double thickness) {
        this.widthFrom = widthFrom;
        this.widthTo = widthTo;
        this.heightFrom = heightFrom;
        this.heightTo = heightTo;
        this.lengthFrom = lengthFrom;
        this.lengthTo = lengthTo;
        this.multiplier = multiplier;
        this.thickness = thickness;
    }
}
