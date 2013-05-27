package com.github.jamesnorris.threading;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.inter.ZARepeatingThread;
import com.github.jamesnorris.util.MiscUtil;
import com.github.jamesnorris.util.Path;
import com.github.jamesnorris.util.Pathfinder;

public class MobTargettingThread implements ZARepeatingThread {
    private Creature creature;
    private DataContainer data = Ablockalypse.getData();
    private int interval = 1, count = 0, nodeNum = 0, standStill = 0, stillTime = 15;
    private HashMap<Integer, double[]> locations = new HashMap<Integer, double[]>();
    private double nodesPerTick = .08D, activeNodesPerTick = 0;
    private Path path;
    private boolean runThrough = false;
    private Location target, previous;

    public MobTargettingThread(Creature creature, Location target, double nodesPerTick, boolean autorun) {
        this.creature = creature;
        this.target = target;
        this.nodesPerTick = nodesPerTick;
        setTarget(target);
        runThrough = autorun;
        addToThreads();
    }

    @Override public int getCount() {
        return count;
    }

    @Override public int getInterval() {
        return interval;
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

    protected void recalculate(Location start, Location finish) {
        path = Pathfinder.calculate(start, finish);
        target = finish;
        nodeNum = 0;
        activeNodesPerTick = 0;
        locations = path.getRawNodesMap();
    }

    @Override public void remove() {
        data.threads.remove(this);
    }

    @Override public void run() {
        if (creature == null || creature.isDead()) {
            remove();
            return;
        }
        Location creatureLoc = getCreatureMovementLocation(creature);
        activeNodesPerTick += nodesPerTick;
        if (activeNodesPerTick >= interval) {
            nodeNum++;
            previous = target;
            activeNodesPerTick = 0;
        }
        int currentDist = 16;
        Player closestPlayer = null;
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getLocation().distance(creatureLoc) < currentDist) {
                closestPlayer = player;
            }
        }
        boolean distantBarrierTarget = target instanceof Location && data.isBarrier(target) && !data.getBarrier(target).isWithinRadius(creature, 1);
        boolean usingRegularPathfinder = closestPlayer != null ? creatureLoc.distance(closestPlayer.getLocation()) <= 15 && creature.getTarget() != null && !creature.getTarget().isDead() : false;
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
            creature.teleport(move);
        }
        standStill = previous != null && MiscUtil.locationMatch(previous, creatureLoc) ? ++standStill : 0;
        if (standStill >= stillTime * 20 / interval) {// same spot for "stillTime" seconds
            standStill = 0;
            recalculate(creatureLoc, target);
        }
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setCount(int i) {
        count = i;
    }

    @Override public void setInterval(int i) {
        interval = i;
    }

    public void setNodesPerTick(double nodesPerTick) {
        this.nodesPerTick = nodesPerTick;
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    public void setStandStillAllowance(int seconds) {
        stillTime = seconds;
    }

    public void setTarget(Location loc) {
        recalculate(getCreatureMovementLocation(creature), loc);
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }
    
    private Location getCreatureMovementLocation(LivingEntity entity) {
        return MiscUtil.floorLivingEntity(entity);
    }
}
