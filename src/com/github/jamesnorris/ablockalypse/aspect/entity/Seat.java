package com.github.jamesnorris.ablockalypse.aspect.entity;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Seat extends PlaceholderEntity {
    public Seat(Location loc) {
        super(loc);
    }

    public void moveLocation(Location loc) {
        this.loc = loc;
        entity.teleport(loc);
    }

    public void removePassenger() {
        if (entity != null) {
            entity.remove();
        }
        entity.setPassenger(null);
    }

    public void sit(Player player) {
        if (loc == null) {
            return;
        }
        if (entity == null) {
            entity = loc.getWorld().spawnEntity(loc, type);
        }
        entity.setPassenger(null);
        entity.setPassenger(player);
    }
}
