package com.github.utility.ranged;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Shot {
    private final Location from;
    private ShotData data;

    public Shot(Location from, ShotData data) {
        this.from = from;
        this.data = data;
    }

    public List<HitBox> arrangeClosest(List<HitBox> hitBoxes) {
        return arrangeClosest(from, hitBoxes);
    }

    public static List<HitBox> arrangeClosest(Location from, List<HitBox> hitBoxes) {
        List<HitBox> arranged = new ArrayList<HitBox>();
        for (int i = 0; i < hitBoxes.size(); i++) {
            HitBox closest = getClosest(from, hitBoxes);
            hitBoxes.remove(closest);
            arranged.add(closest);
        }
        return arranged;
    }

    public HitBox getClosest(List<HitBox> hitBoxes) {
        return getClosest(from, hitBoxes);
    }

    public static HitBox getClosest(Location from, List<HitBox> hitBoxes) {
        HitBox closest = hitBoxes.get(0);
        for (HitBox hitBox : hitBoxes) {
            //TODO by closest corner, not by center
            if (hitBox.getCenter().distance(from) < closest.getCenter().distance(from)) {
                closest = hitBox;
            }
        }
        return closest;
    }

    /* 
     * TODO Checking for obstacles
     * TODO Only allow parts of the HitBox to be hit that are in range
     * TODO Speed in Blocks Per Second, as contained in ShotData
     * TODO Projectile penetration
     */
    public List<Hit> shoot(List<HitBox> hitBoxes) {
        List<Hit> hits = new ArrayList<Hit>();
        for (HitBox hitBox : hitBoxes) {
            hitBox.update();
            float fromYaw = MathUtility.absDegrees(from.getYaw());
            float fromPitch = MathUtility.absDegrees(from.getPitch());
            if (hitBox.getCenter()/*closest corner*/.distanceSquared(from) > Math.pow(data.getDistanceToTravel(), 2)) {
                continue;
            }
            float windCompassDirection = MathUtility.absDegrees(data.getWindCompassDirection(from.getWorld()));
            float windSpeed = data.getWindSpeedMPH(from.getWorld());
            fromYaw += (windCompassDirection > fromYaw ? 1 : windCompassDirection < fromYaw ? -1 : 0) * windSpeed;
            fromYaw = MathUtility.absDegrees(fromYaw);
            int[] orderClockwise = new int[] {0, 1, 4, 3};
            Location leftViewCorner = hitBox.getCorner(0);
            double leftViewCornerYaw = Math.atan2(leftViewCorner.getX() - from.getX(), leftViewCorner.getZ() - from.getZ()) * 180 / Math.PI;// flipped x and z from normal;
            int leftViewCornerIndex = 0;
            for (int index = 0; index < orderClockwise.length; index++) {
                int number = orderClockwise[index];
                Location corner = hitBox.getCorner(number);
                double yawToCorner = Math.atan2(corner.getX() - from.getX(), corner.getZ() - from.getZ()) * 180 / Math.PI;// flipped x and z from normal
                if (yawToCorner > leftViewCornerYaw) {
                    leftViewCorner = corner;
                    leftViewCornerYaw = yawToCorner;
                    leftViewCornerIndex = index;
                }
            }
            Location leftFarCorner = hitBox.getCorner((leftViewCornerIndex + 1) % 3);
            Location entrance = getProjectileLocation(leftViewCorner, hitBox, fromYaw, fromPitch);
            double entranceDistance = entrance.distance(from);
            entrance.add(data.getDeltaX(entranceDistance, fromYaw), data.getDeltaY(entranceDistance, fromPitch), data.getDeltaZ(entranceDistance, fromYaw));
            Location exit = getProjectileLocation(leftFarCorner, hitBox, fromYaw, fromPitch);
            double exitDistance = exit.distance(from);
            exit.add(data.getDeltaX(exitDistance, fromYaw), data.getDeltaY(exitDistance, fromPitch), data.getDeltaZ(exitDistance, fromYaw));
            double padding = hitBox.getPadding();
            boolean hitX = entrance.getX() <= hitBox.getHighestX() + padding && entrance.getX() >= hitBox.getLowestX() - padding;
            boolean hitY = entrance.getY() <= hitBox.getHighestY() + padding && entrance.getY() >= hitBox.getLowestY() - padding;
            boolean hitZ = entrance.getZ() <= hitBox.getHighestZ() + padding && entrance.getZ() >= hitBox.getLowestZ() - padding;
            //System.out.println(entrance + "\n\n" + hitBox.getCenter() + "\n\n" + hitX + ", " + hitY + ", " + hitZ);//TODO remove
            if (hitX && hitY && hitZ) {
                hits.add(new Hit(from, entrance, exit, hitBox, data));
            }
        }
        return hits;
    }

    private Location getProjectileLocation(Location leftViewCorner, HitBox hitBox, float fromYaw, float fromPitch) {//TODO this method is not accurate
        double deltaFromToSideCornerX = leftViewCorner.getX() - from.getX();
        double deltaFromToSideCornerY = leftViewCorner.getY() - from.getY();
        double deltaFromToSideCornerZ = leftViewCorner.getZ() - from.getZ();
        fromYaw = (fromYaw + 180) % 360;//To account for backwards yaw in MC
        double xzDistFromSideCorner = new Location(from.getWorld(), from.getX() + deltaFromToSideCornerX, from.getY(), from.getZ() + deltaFromToSideCornerZ, from.getYaw(), from.getPitch()).distance(from);//Math.sqrt(Math.pow(deltaFromToSideCornerX, 2) + Math.pow(deltaFromToSideCornerZ, 2));
        double yawToSideCorner = Math.atan2(Math.abs(deltaFromToSideCornerX), Math.abs(deltaFromToSideCornerZ)) * 180 / Math.PI;// flipped x and z from normal
        double theta1 = yawToSideCorner - (MathUtility.absDegrees(fromYaw) % 90);
        //double theta2 = yawToSideCorner - theta1;aka fromYaw
        double outerAngle = 90 - yawToSideCorner;
        double outerAngleAppendage = (hitBox.getYawRotation() + 360) % 90;
        double outerAngleInShotCone = outerAngle + outerAngleAppendage;
        double lastAngleInShotCone = (180 + outerAngleAppendage) - theta1 - outerAngleInShotCone;
        //System.out.println(xzDistFromSideCorner + ", " + outerAngleInShotCone + ", " + lastAngleInShotCone);//TODO remove
        double xzDistanceFromHit = (xzDistFromSideCorner * Math.sin(Math.toRadians(outerAngleInShotCone))) / Math.sin(Math.toRadians(lastAngleInShotCone));
        double deltaX = xzDistanceFromHit * Math.sin(Math.toRadians(fromYaw % 90/*theta2*/));
        double deltaZ = xzDistanceFromHit * Math.cos(Math.toRadians(fromYaw % 90/*theta2*/));
        double xyzDistFromSideCorner = Math.sqrt(Math.pow(deltaFromToSideCornerX, 2) + Math.pow(deltaFromToSideCornerY, 2) + Math.pow(deltaFromToSideCornerZ, 2));
        double theta3 = Math.atan2(Math.abs(deltaFromToSideCornerY), xzDistFromSideCorner) * 180 / Math.PI;
        double theta4 = Math.abs(fromPitch) - theta3;
        double theta5 = 90 + theta3;
        double theta6 = 180 - theta4 - theta5;
        double hitDistance = (xyzDistFromSideCorner * Math.sin(Math.toRadians(theta5))) / Math.sin(Math.toRadians(theta6));
        double deltaY = hitDistance * Math.sin(Math.toRadians(Math.abs(fromPitch)));
        if (fromYaw > 180 && deltaX > 0) {
            deltaX *= -1;
        }
        if (fromPitch > 0 && deltaY > 0) {// pitch and yaw in MC are backwards
            deltaY *= -1;
        }
        if (fromYaw < 270 && fromYaw > 90 && deltaZ > 0) {
            deltaZ *= -1;
        }
        //System.out.println(xzDistanceFromHit + ", " + fromYaw + ", " + deltaX);//TODO remove
        return new Location(from.getWorld(), from.getX() + deltaX, from.getY() + deltaY, from.getZ() + deltaZ, fromYaw, fromPitch);
    }
}
