package com.github.utility.ranged;

import org.bukkit.Location;

public class Ray3D {
    public Ray3D(Location orig, Location dir) {
        this.orig = orig;
        this.dir = dir;
        this.tmin = 0;
        this.tmax = Double.MAX_VALUE;
        this.invdir = new Location(dir.getWorld(), 1.0D / dir.getX(), 1.0D / dir.getY(), 1.0D / dir.getZ());
        this.sign[0] = (invdir.getX() < 0) ? 1 : 0;
        this.sign[1] = (invdir.getY() < 0) ? 1 : 0;
        this.sign[2] = (invdir.getZ() < 0) ? 1 : 0;
    }

    Location orig, dir; // / ray orig and dir
    double tmin, tmax; // / ray min and max distances
    Location invdir;
    int[] sign = new int[3];
}
