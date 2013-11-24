package com.github.jamesnorris.ablockalypse.aspect.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;

public class PlaceholderEntity {
    protected static final EntityType type = EntityType.ARROW;
    protected DataContainer data = Ablockalypse.getData();
    protected Entity entity;
    protected Location loc;

    public PlaceholderEntity(Location loc) {
        data.objects.add(this);
        entity = loc.getWorld().spawnEntity(loc, type);
        entity.setTicksLived(Integer.MAX_VALUE);
    }

    public Entity getEntity() {
        return entity;
    }

    public Location getLocation() {
        return loc;
    }

    public void remove() {
        if (entity != null) {
            entity.remove();
        }
        data.objects.remove(this);
    }
}
