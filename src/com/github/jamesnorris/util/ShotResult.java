package com.github.jamesnorris.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ShotResult {
    private HashMap<LivingEntity, Location> hits = new HashMap<LivingEntity, Location>();
    private Vector direction;
    
    public ShotResult(HashMap<LivingEntity, Location> hits, Vector direction) {
        this.hits = hits;
        this.direction = direction;
    }
    
    public Vector getShotDirection() {
        return direction;
    }
    
    public Set<LivingEntity> getLivingEntitiesHit() {
        return hits.keySet();
    }
    
    public Location getLocationWhereHit(LivingEntity ent) {
        return hits.get(ent);
    }
    
    public Collection<Location> getHitLocations() {
        return hits.values();
    }
}
