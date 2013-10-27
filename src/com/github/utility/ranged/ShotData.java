package com.github.utility.ranged;

import org.bukkit.World;

public interface ShotData {
    public float getWindSpeedMPH(World world);
    
    public float getWindCompassDirection(World world);
    
    public double getDistanceToTravel();
    
    public double getStartingPenetration();
    
    public double getPenetration(double current, double distance);
    
    public double getDamage(double distance);
    
    public float getSpeedBPS(double distance);
    //TODO add comments that let the user know that all yaw and pitch arguments passed should be in normalized (real) form, and not notch's nonsense
    public double getDeltaX(double distance, float originalYaw);
    
    public double getDeltaY(double distance, float originalPitch);
    
    public double getDeltaZ(double distance, float originalYaw);
}
