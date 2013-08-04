package com.github.utility;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;

public class Seat {
    private static final EntityType type = EntityType.ARROW;
    private DataContainer data = Ablockalypse.getData();
    private Entity entity;
    private Location loc;

    public Seat(Location loc) {
        this.loc = loc;
        entity = null;
        data.objects.add(this);
    }

    public Location getLocation() {
        return loc;
    }

    //can return null
    public Entity getSeatEntity() {
        return entity;
    }

    public void moveLocation(Location loc) {
        this.loc = loc;
    }

    public void remove() {
        if (entity != null) {
            entity.remove();
        }
        data.objects.remove(this);
    }

    public void removePassenger() {
        if (entity != null) {
            entity.remove();
        }
        entity.setPassenger(null);
    }

    public void sit(Player player) {// TODO fix a bug where when the player attempts to pick himself up it kicks them. Also, then disable the ability to pick themselves up.
        if (entity == null) {
            entity = loc.getWorld().spawnEntity(loc, type);
        }
        entity.setPassenger(null);
        entity.setPassenger(player);
    }
}
