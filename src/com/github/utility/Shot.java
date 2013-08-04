package com.github.utility;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Shot {
    private final Player shooter;
    private final Location shooterPosition;
    private final Vector direction;
    private int precision;

    public Shot(Player shooter) {
        this(shooter, 10);
    }

    public Shot(Player shooter, int precision) {
        this.shooter = shooter;
        this.precision = precision;
        shooterPosition = shooter.getEyeLocation();
        direction = shooterPosition.getDirection();
    }

    public int getPrecision() {
        return precision;
    }

    public Player getShooter() {
        return shooter;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public ShotResult shoot(int distance, int damage) {
        return shoot(distance, 1, damage, true, true);
    }

    public ShotResult shoot(int distance, int penetration, int damage) {
        return shoot(distance, penetration, damage, true, true);
    }

    public ShotResult shoot(int distance, int penetration, int damage, boolean wallsAffectPenetration, boolean hitsAffectPenetration) {
        HashMap<LivingEntity, Location> hits = new HashMap<LivingEntity, Location>();
        try {
            ArrayList<Location> nonEntityHits = new ArrayList<Location>();
            int lastSince = 15;
            Location chunkLoc = null;
            for (int i = 0; i < distance; i++) {
                chunkLoc = shooterPosition.clone().add(direction);
                ++lastSince;
                if (lastSince >= 16) {
                    lastSince = 0;
                }
                for (int div = precision; div > 0; div--) {// the higher the precision integer, the greater the accuracy
                    Vector shot = direction.clone().multiply(i + 1 / div);
                    if (!shot.toLocation(shooterPosition.getWorld()).getBlock().isEmpty() && wallsAffectPenetration && !nonEntityHits.contains(shot.toLocation(shooterPosition.getWorld()))) {
                        --penetration;
                        nonEntityHits.add(shot.toLocation(shooterPosition.getWorld()));
                    }
                    if (chunkLoc != null && penetration > 0) {
                        for (Entity e : chunkLoc.getChunk().getEntities()) {
                            if (e instanceof LivingEntity) {
                                LivingEntity ent = (LivingEntity) e;
                                Location loc = ent.getEyeLocation();
                                double height = ent.getEyeHeight();
                                Object nmsEntity = MiscUtil.getMethod(e.getClass(), "getHandle").invoke(ent, (Object[]) null);
                                double width = MiscUtil.getField(nmsEntity.getClass(), "width").getDouble(nmsEntity);
                                double length = MiscUtil.getField(nmsEntity.getClass(), "length").getDouble(nmsEntity);
                                float thetaOne = Math.abs(loc.getYaw() - shooterPosition.getYaw());
                                float thetaTwo = 90 - thetaOne;
                                double viewWidth = width * Math.cos(thetaOne) + length * Math.cos(thetaTwo);
                                double Xdif = loc.getX() - shooterPosition.getX();
                                double Ydif = loc.getY() - shooterPosition.getY() - height;// foot Y
                                double Zdif = loc.getZ() - shooterPosition.getZ();
                                boolean Xhit = shot.getX() <= Xdif + viewWidth && shot.getX() >= Xdif - viewWidth;
                                boolean Yhit = shot.getY() <= Ydif + height && shot.getY() >= Ydif;
                                boolean Zhit = shot.getZ() <= Zdif + viewWidth && shot.getZ() >= Zdif - viewWidth;
                                if (Xhit && Yhit && Zhit && !hits.containsValue(ent.getLocation())) {
                                    hits.put(ent, ent.getLocation());
                                    ent.damage(damage, shooter);
                                    if (hitsAffectPenetration) {
                                        --penetration;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ShotResult(hits, direction);
    }
}
