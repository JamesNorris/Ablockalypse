package com.github.jamesnorris.ablockalypse.threading.inherent;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;

import com.github.jamesnorris.ablockalypse.aspect.ZAMob;
import com.github.jamesnorris.ablockalypse.behavior.Targettable;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.threading.DelayedTask;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;
import com.github.jamesnorris.ablockalypse.utility.AblockalypseUtility;
import com.github.jamesnorris.ablockalypse.utility.BukkitUtility;
import com.github.jamesnorris.mcpath.PathNode;
import com.github.jamesnorris.mcpath.Pathfinder;

public class MobTargetterTask extends RepeatingTask {
    public class StandStillPreventorTask extends RepeatingTask {
        private int warnLevel;
        private MobTargetterTask task;
        private Location lastMoved;

        public StandStillPreventorTask(MobTargetterTask task, ZAMob mob) {
            super(20, true);
            this.task = task;
            lastMoved = mob.getEntity().getLocation();
        }

        @Override public void run() {
            if (mob == null || !mob.isValid()) {
                cancel();
                return;
            }
            Location updated = mob.getEntity().getLocation();
            if (task.target == null) {
                return;
            }
            if (BukkitUtility.locationMatch(updated, lastMoved, 1) && !BukkitUtility.locationMatch(updated, task.target.updateTarget(), 1)) {
                warnLevel++;
                if (warnLevel >= 5) {
                    task.forceSafeDistance(updated, task.target.updateTarget());
                }
            } else {
                lastMoved = updated;
                warnLevel = 0;
            }
        }
    }

    private static final int INTERVAL = 1;
    private int max = (Integer) Setting.MAX_PATHFINDER_NODES.getSetting();
    private double current = 0;
    private ZAMob mob;
    private Targettable target;
    private List<PathNode> nodes;
    private StandStillPreventorTask ssPrevent;

    public MobTargetterTask(ZAMob mob, final Location target, boolean autorun) {
        this(mob, new Targettable() {
            @Override public boolean isResponsive() {
                return true;
            }

            @Override public boolean isTargettedBy(ZAMob mob) {
                return false;
            }

            @Override public Location updateTarget() {
                return target;
            }
        }, autorun);
    }

    public MobTargetterTask(ZAMob mob, Targettable target, boolean autorun) {
        super(INTERVAL, autorun);
        this.mob = mob;
        this.target = target;
        ssPrevent = new StandStillPreventorTask(this, mob);
    }

    @Override public void cancel() {
        ssPrevent.cancel();
        super.cancel();
    }

    public void panic(int ticks) {
        final Location tempTarget = target != null ? target.updateTarget() : mob.getEntity().getLocation();
        new RepeatingTask(ticks / 10, true) {
            @Override public void run() {
                setTarget(BukkitUtility.getNearbyLocation(tempTarget, 5, 15, 0, 0, 5, 15));
            }
        };
        new DelayedTask(ticks, true) {
            @Override public void run() {
                setTarget(tempTarget);
            }
        };
    }

    @Override public void run() {
        if (mob == null || !mob.isValid()) {
            cancel();
            return;
        }
        if (target == null) {
            return;
        }
        if (!target.isResponsive()) {
            mob.retarget();
        }
        if (target == null) {
            return;//2nd check to make sure mob.retarget() didn't set the target to null
        }
        World world = mob.getEntity().getWorld();
        ensureSameWorld(world);
        updatePath();
        ensureSafeDistance();
        Location to = nodes.size() > (int) current ? nodes.get((int) current).toLocation(world) : nodes.get(nodes.size() - 1).toLocation(world);
        if (to == null) {
            return;
        }
        if (nodes.size() > (int) current + 1) {
            to = between(to, nodes.get((int) current + 1).toLocation(world), current % 1);
        }
        raise(to);
        if (to.getBlock().getRelative(0, -1, 0).isEmpty()) {
            return;// delay until ground is hit
        }
        setLocationDirection(to, target.updateTarget());
        if (!target.isTargettedBy(mob)) {
            if (mob.getEntity() instanceof Creature) {
                // prevents wandering
                ((Creature) mob.getEntity()).setTarget(null);
            }
            mob.getEntity().teleport(to);
            current += mob.getSpeed();
        }
    }

    public void setTarget(final Location target) {
        setTarget(new Targettable() {
            @Override public boolean isResponsive() {
                return true;
            }

            @Override public boolean isTargettedBy(ZAMob mob) {
                return false;
            }

            @Override public Location updateTarget() {
                return target;
            }
        });
    }

    public void setTarget(Targettable target) {
        this.target = target;
        if (mob != null && mob.getEntity() != null && !mob.getEntity().isDead() && target != null) {
            updatePath();
        }
    }

    private Location between(Location loc1, Location loc2, double part) {
        return new Location(loc1.getWorld(), loc1.getX() + (loc2.getX() - loc1.getX()) * part, loc1.getY() + (loc2.getY() - loc1.getY()) * part, loc1.getZ() + (loc2.getZ() - loc1.getZ()) * part);
    }

    private void ensureSafeDistance() {
        Location start = mob.getEntity().getLocation();
        Location finish = target.updateTarget();
        if (start.distance(finish) > max) {
            forceSafeDistance(start, finish);
        }
    }

    private void ensureSameWorld(World world) {
        Location updatedTarget = target.updateTarget();
        if (!world.getUID().equals(updatedTarget.getWorld().getUID())) {
            Location to = mob.getEntity().getLocation().clone();
            to.setWorld(updatedTarget.getWorld());
            mob.getEntity().teleport(to);
        }
    }

    private void forceSafeDistance(Location start, Location finish) {
        mob.getEntity().teleport(BukkitUtility.getNearbyLocation(start, max * (6 / 8), max * (7 / 8), max * (6 / 8), max * (7 / 8), max * (6 / 8), max * (7 / 8)));
    }

    private void raise(Location move) {
        move.setY(mob.getEntity().getLocation().getY());
    }

    private void setLocationDirection(Location current, Location look) {
        float yaw = (float) Math.toDegrees(Math.atan((current.getZ() - look.getZ()) / (current.getX() - look.getX())));
        float pitch = (float) Math.toDegrees(Math.atan((current.getY() - look.getY()) / (current.getX() - look.getX())));
        current.setYaw(360 - yaw % 360/* back to notchian */);
        current.setPitch(pitch);
    }

    private void updatePath() {
        Location eLoc = mob.getEntity().getLocation();
        Location updatedTarget = target.updateTarget();
        if (nodes == null || updatedTarget.distance(nodes.get(nodes.size() - 1).toLocation(updatedTarget.getWorld())) >= 2 && PathNode.fromLocation(eLoc).distance(nodes.get(0)) >= 2) {
            current %= 1;
            nodes = new Pathfinder(eLoc, updatedTarget, AblockalypseUtility.getGoals(updatedTarget.getWorld(), mob.getLength(), mob.getWidth(), mob.getHeight(), mob.getHitBox().getRotationY())).calculate((Integer) Setting.MAX_PATHFINDER_NODES.getSetting()).getNodes();
        }
    }
}
