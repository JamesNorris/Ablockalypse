package com.github.jamesnorris.ablockalypse.utility;

import org.bukkit.World;
import org.bukkit.block.Biome;

import com.github.jamesnorris.mcshot.ShotData;

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

    @Override public double getInitialPenetration() {
        return 0;
    }

    @Override public double getPenetration(double distance) {
        return 50;
    }

    @Override public float getSpeedBPS(double distance) {
        return 100;
    }

    @Override public float getWindCompassDirection(World world) {
        return 0;
    }

    @Override public float getWindSpeedMPH(World world) {
        return 0;
    }

    @Override public float getWindSpeedMPH(World world, Biome[] biomes) {
        return 0;
    }
}
