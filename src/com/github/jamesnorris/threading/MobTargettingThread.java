package com.github.jamesnorris.threading;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.inter.ZARepeatingThread;
import com.github.jamesnorris.util.Breakable;
import com.github.jamesnorris.util.MiscUtil;
import com.github.jamesnorris.util.Path;
import com.github.jamesnorris.util.Pathfinder;

public class MobTargettingThread extends DataManipulator implements ZARepeatingThread {
    private boolean runThrough = false, recalculate = false;
    private int interval = 1, count = 0, nodeNum = 0, standStill = 0;
    private HashMap<Integer, double[]> locations = new HashMap<Integer, double[]>();
    private double nodesPerTick = .05D, activeNodesPerTick;
    private Location previous;
    private Game game;
    private Path path;
    private Creature creature;
    private Object target;

    public MobTargettingThread(Creature creature, Object target, boolean autorun) {
        this.creature = creature;
        this.target = target;
        setTarget(target);
        setRunThrough(autorun);
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }
    
    public Path getPath() {
        return path;
    }
    
    public void setNodesPerTick(double nodesPerTick) {
        this.nodesPerTick = nodesPerTick;
    }
    
    public boolean hasPlayerTarget() {
        return target instanceof Player;
    }
    
    public Object getTarget() {
        return target;
    }
    
    public double getNodesPerTick() {
        return nodesPerTick;
    }
    
    public Location getTargetLocation() {
        return (target instanceof Player) ? ((Player) target).getLocation() : (Location) target;
    }

    public void setTarget(Object obj) {
        if (obj instanceof Player) {
            setPlayerTarget((Player) obj);
        } else if (obj instanceof Location) {
            setLocationTarget((Location) obj);
        }
    }

    public void setPlayerTarget(Player player) {
        target = player;
        path = Pathfinder.calculate(creature.getLocation(), player.getLocation());
        locations = path.getRawNodesMap();
        nodeNum = 0;
    }

    public void setLocationTarget(Location loc) {
        target = loc;
        path = Pathfinder.calculate(creature.getLocation(), loc);
        locations = path.getRawNodesMap();
        nodeNum = 0;
    }

    public void allowGameFunctions(Game game) {
        this.game = game;
    }

    public void disallowGameFunctions() {
        this.game = null;
    }

    @Override public void run() {
        if (creature == null || creature.isDead()) {
            remove();
            return;
        }
        Location creatureLoc = creature.getLocation();
        if (hasPlayerTarget()) {
            if (((Player) target).isDead()) {
                if (game != null) {
                    setLocationTarget(game.getSpawnManager().getClosestBarrier(creatureLoc).getCenter());
                    return;
                } else {
                    remove();
                }
            }
            nodeNum = 0;
            recalculate = true;
            locations = path.getRawNodesMap();
        }
        Location targetToLocation = (target instanceof Player) ? ((Player) target).getLocation() : (Location) target;
        activeNodesPerTick += nodesPerTick;
        if (activeNodesPerTick >= interval) {
            nodeNum++;
            activeNodesPerTick = nodesPerTick;
        }
        standStill = (previous != null && MiscUtil.locationMatch(previous, creatureLoc)) ? ++standStill : 0;
        if (standStill >= (100 / interval) && !data.isBarrier(targetToLocation)) {// same spot for 5 seconds
            standStill = 0;
            if (game != null && game.getPlayers().size() > 0) {
                setPlayerTarget(game.getRandomLivingPlayer());
            }
            recalculate = true;
        }
        if (recalculate) {
            recalculate = false;
            path = Pathfinder.calculate(creatureLoc, targetToLocation);
            locations = path.getRawNodesMap();
        }
        previous = creatureLoc;
        int currentDist = 17;
        Player closestPlayer = null;
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getLocation().distance(creatureLoc) < currentDist) {
                closestPlayer = player;
            }
        }
        boolean distantBarrierTarget = target instanceof Location && data.isBarrier((Location) target) && !data.getBarrier((Location) target).isWithinRadius(creature);
        boolean usingRegularPathfinder = (closestPlayer != null) ? creatureLoc.distance(closestPlayer.getLocation()) <= 15 && creature.getTarget() != null && !creature.getTarget().isDead() : false;
        double[] coords = locations.get(nodeNum);//current coordinates
        if (coords != null && (!usingRegularPathfinder || distantBarrierTarget)) {
            double[] futureCoords = locations.get(nodeNum + 1);
            double Xadd = 0, Yadd = 0, Zadd = 0;
            if (futureCoords != null) {
                Xadd = (futureCoords[0] - coords[0]) * activeNodesPerTick;
                Yadd = (futureCoords[1] - coords[1]) * activeNodesPerTick;
                Zadd = (futureCoords[2] - coords[2]) * activeNodesPerTick;
            }
            Location tele = new Location(targetToLocation.getWorld(), coords[0] + Xadd, coords[1] + Yadd, coords[2] + Zadd);
            Breakable.getNMSEntity(creature).setPosition(tele.getX(), tele.getY(), tele.getZ());//fairly smooth
        }
    }

    @Override public void remove() {
        data.threads.remove(this);
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    @Override public int getCount() {
        return count;
    }

    @Override public int getInterval() {
        return interval;
    }

    @Override public void setCount(int i) {
        count = i;
    }

    @Override public void setInterval(int i) {
        interval = i;
    }
}