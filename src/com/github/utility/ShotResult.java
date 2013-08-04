package com.github.utility;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ShotResult {
    private Vector direction;
    private HashMap<LivingEntity, Location> hits = new HashMap<LivingEntity, Location>();

    public ShotResult(HashMap<LivingEntity, Location> hits, Vector direction) {
        this.hits = hits;
        this.direction = direction;
    }

    public Collection<Location> getHitLocations() {
        return hits.values();
    }

    public Set<LivingEntity> getLivingEntitiesHit() {
        return hits.keySet();
    }

    public Location getLocationWhereHit(LivingEntity ent) {
        return hits.get(ent);
    }

    public Vector getShotDirection() {
        return direction;
    }
}
