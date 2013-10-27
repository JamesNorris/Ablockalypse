package com.github.utility.ranged;

import org.bukkit.Location;

public class Box3D {
    private Location[] bounds = new Location[2];
    
    public Box3D(Location vmin, Location vmax)
    {
        this.bounds[0] = vmin;
        this.bounds[1] = vmax;
    }
    
    public void update(Location vmin, Location vmax) {
        this.bounds[0] = vmin;
        this.bounds[1] = vmax;
    }

    public boolean intersect(Ray3D r)
    {
        double tmin, tmax, tymin, tymax, tzmin, tzmax;
        tmin = (this.bounds[r.sign[0]].getX() - r.orig.getX()) * r.invdir.getX();
        tmax = (this.bounds[1 - r.sign[0]].getX() - r.orig.getX()) * r.invdir.getX();
        tymin = (this.bounds[r.sign[1]].getY() - r.orig.getY()) * r.invdir.getY();
        tymax = (this.bounds[1 - r.sign[1]].getY() - r.orig.getY()) * r.invdir.getY();
        if ((tmin > tymax) || (tymin > tmax))
            return false;
        if (tymin > tmin)
            tmin = tymin;
        if (tymax < tmax)
            tmax = tymax;
        tzmin = (this.bounds[r.sign[2]].getZ() - r.orig.getZ()) * r.invdir.getZ();
        tzmax = (this.bounds[1 - r.sign[2]].getZ() - r.orig.getZ()) * r.invdir.getZ();
        if ((tmin > tzmax) || (tzmin > tmax))
            return false;
        if (tzmin > tmin)
            tmin = tzmin;
        if (tzmax < tmax)
            tmax = tzmax;
        if (tmin > r.tmin) r.tmin = tmin;
        if (tmax < r.tmax) r.tmax = tmax;
        return true;
    }
}
