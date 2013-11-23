package com.github.utility.ranged.type;

import org.bukkit.World;

import com.github.utility.ranged.ShotData;

public class HitThroughWallShot implements ShotData {
    private double damage;

    public HitThroughWallShot(double damage) {
        this.damage = damage;
    }

    @Override public double getDamage(double distance) {
        return damage;
    }

    @Override public double getDeltaX(double distance, float originalYaw) {
        return 0;
    }

    @Override public double getDeltaY(double distance, float originalPitch) {
        return 0;
    }

    @Override public double getDeltaZ(double distance, float originalYaw) {
        return 0;
    }

    @Override public double getDistanceToTravel() {
        return 4;
    }

    @Override public double getPenetration(double current, double distance) {
        return getStartingPenetration();
    }

    @Override public float getSpeedBPS(double distance) {
        return 100;
    }

    @Override public double getStartingPenetration() {
        return .0000000001;
    }

    @Override public float getWindCompassDirection(World world) {
        return 0;
    }

    @Override public float getWindSpeedMPH(World world) {
        return 0;
    }
}
