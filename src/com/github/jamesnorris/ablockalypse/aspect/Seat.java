package com.github.jamesnorris.ablockalypse.aspect;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.jamesnorris.ablockalypse.utility.MathUtility;

public class Seat extends PlaceholderEntity {
    private Player passenger;
    private Location save;

    public Seat(Location loc) {
        super(adaptedLocation(loc));
    }

    public void moveLocation(Location loc) {
        this.loc = adaptedLocation(loc);
        ensureEntity();
        entity.teleport(this.loc);
    }

    private static Location adaptedLocation(Location loc) {
        double yaw = MathUtility.absDegrees(loc.getYaw() * -1);
        return loc.clone().subtract(Math.cos(yaw * Math.PI / 180), -.2, Math.sin(yaw * Math.PI / 180));
    }

    private void ensureEntity() {
        if (entity != null && !entity.isDead()) {
            return;
        }
        entity = loc.getWorld().spawnEntity(loc, type);
    }

    public Player getPassenger() {
        return passenger;
    }

    public boolean removePassenger() {
        if (entity == null || entity.getPassenger() == null) {
            return false;
        }
        passenger.getLocation().setY(save.getY());
        passenger.teleport(save);
        passenger = null;
        entity.remove();
        return true;
    }

    public void sit(Player player) {
        if (loc == null) {
            return;
        }
        ensureEntity();
        player.setSneaking(false);
        passenger = player;
        save = player.getLocation();
        entity.setPassenger(player);
    }
}
