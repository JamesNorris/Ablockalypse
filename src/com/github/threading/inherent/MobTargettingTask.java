package com.github.threading.inherent;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.intelligent.Path;
import com.github.threading.RepeatingTask;
import com.github.utility.BukkitUtility;
import com.github.utility.Pathfinder;


public class MobTargettingTask extends RepeatingTask {
    private static final int INTERVAL = 1;
    private LivingEntity entity;
    private DataContainer data = Ablockalypse.getData();
    private int nodeNum = 0, standStill = 0, stillTime = 5;
    private HashMap<Integer, double[]> locations = new HashMap<Integer, double[]>();
    private double nodesPerTick = .08D, activeNodesPerTick = 0;
    private Path path;
    private Location target, previous;

    public MobTargettingTask(LivingEntity entity, Location target, double nodesPerTick, boolean autorun) {
        super(INTERVAL, autorun);
        this.entity = entity;
        this.target = target;
        this.nodesPerTick = nodesPerTick;
        setTarget(target);
    }

    public double getNodesPerTick() {
        return nodesPerTick;
    }

    public Path getPath() {
        return path;
    }

    public int getStandStillAllowance() {
        return stillTime / 20;
    }

    public int getStandStillTime() {
        return standStill / 20;
    }

    public Location getTarget() {
        return target;
    }

    @Override public void run() {
        if (entity == null || entity.isDead()) {
            cancel();
            return;
        }
        if (target == null) {
            if (data.isZAMob(entity)) {
                data.getZAMob(entity).retarget();
                cancel();
            }
            return;
        }
        Location creatureLoc = getCreatureMovementLocation(entity);
        activeNodesPerTick += nodesPerTick;
        if (activeNodesPerTick >= INTERVAL) {
            nodeNum++;
            previous = target;
            activeNodesPerTick = 0;
        }
        double currentDist = 256;// 16 squared
        Player closestPlayer = null;
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getLocation().getWorld().getName().equalsIgnoreCase(creatureLoc.getWorld().getName()) && player.getLocation().distanceSquared(creatureLoc) < currentDist) {
                closestPlayer = player;
            }
        }
        boolean distantBarrierTarget = target instanceof Location && data.isBarrier(target) && !(data.getBarrier(target).getCenter().distanceSquared(creatureLoc) < 4);//within 2 blocks
        boolean usingRegularPathfinder = closestPlayer != null && creatureLoc.distanceSquared(closestPlayer.getLocation()) <= 225 /* 15 squared */&& (!(entity instanceof Creature) || ((Creature) entity).getTarget() != null && !((Creature) entity).getTarget().isDead());
        double[] coords = locations.get(nodeNum);
        if (coords != null && (!usingRegularPathfinder || distantBarrierTarget)) {
            previous = creatureLoc;
            double[] futureCoords = locations.get(nodeNum + 1);
            double Xadd = 0, Yadd = 0, Zadd = 0, pitchAdd = 0, yawAdd = 0;
            if (futureCoords != null) {
                Xadd = (futureCoords[0] - coords[0]) * activeNodesPerTick;
                Yadd = (futureCoords[1] - coords[1]) * activeNodesPerTick;
                Zadd = (futureCoords[2] - coords[2]) * activeNodesPerTick;
                yawAdd = (futureCoords[3] - coords[3]) * activeNodesPerTick;
                pitchAdd = (futureCoords[4] - coords[4]) * activeNodesPerTick;
            }
            Location move = new Location(target.getWorld(), coords[0] + Xadd, coords[1] + Yadd, coords[2] + Zadd, (float) (coords[3] + yawAdd), (float) (coords[4] + pitchAdd));
            if (move.clone().subtract(0, 1, 0).getBlock().isEmpty()) {
                recalculate(move, target);
            }
            entity.teleport(move);
        }
        standStill = previous != null && BukkitUtility.locationMatch(previous, creatureLoc) ? ++standStill : 0;
        if (standStill >= stillTime * 20 / INTERVAL) {// same spot for "stillTime" seconds
            standStill = 0;
            recalculate(creatureLoc, target);
        }
    }

    public void setNodesPerTick(double nodesPerTick) {
        this.nodesPerTick = nodesPerTick;
    }

    public void setStandStillAllowance(int seconds) {
        stillTime = seconds;
    }

    public void setTarget(Location loc) {
        recalculate(getCreatureMovementLocation(entity), loc);
    }

    private Location getCreatureMovementLocation(LivingEntity entity) {
        return BukkitUtility.floorLivingEntity(entity);
    }

    protected void recalculate(Location start, Location finish) {
        path = Pathfinder.calculate(start, finish == null ? start : finish);
        target = finish;
        nodeNum = 0;
        activeNodesPerTick = 0;
        locations = path.getRawNodesMap();
    }
}
