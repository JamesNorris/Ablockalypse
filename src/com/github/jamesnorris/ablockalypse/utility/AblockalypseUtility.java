package com.github.jamesnorris.ablockalypse.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.Hellhound;
import com.github.jamesnorris.ablockalypse.aspect.ZAPlayer;
import com.github.jamesnorris.ablockalypse.aspect.Zombie;
import com.github.jamesnorris.ablockalypse.threading.DelayedTask;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;
import com.github.jamesnorris.mcpath.PathNode;
import com.github.jamesnorris.mcpath.PathfinderGoal;
import com.github.jamesnorris.mcshot.DataZone;
import com.github.jamesnorris.mcshot.type.EntityHitBox;

public class AblockalypseUtility {
    private static DataContainer data = Ablockalypse.getData();
    private static HashMap<String, Long> messagesSent = new HashMap<String, Long>();

    public static EntityHitBox createHellhoundHitBox(Hellhound hound) {
        Entity entity = hound.getWolf();
        World world = entity.getWorld();
        EntityHitBox hitBox = new EntityHitBox(entity, hound.getLength(), hound.getWidth(), hound.getHeight());
        Location from = hitBox.getFrom().toLocation(world);
        Location to = hitBox.getTo().toLocation(world);
        double boxX = Math.min(from.getX(), to.getX());
        double boxY = Math.min(from.getY(), to.getY());
        double boxZ = Math.min(from.getZ(), to.getZ());
        hitBox.addZone(new DataZone(world, new Vector(boxX, boxY + .45, boxZ + .1), new Vector(boxX + .5, boxY + .969, boxZ + .4), 2, .75));// head
        hitBox.addZone(new DataZone(world, new Vector(boxX + .4, boxY, boxZ), new Vector(boxX + 1.594, boxY + .969, boxZ + .5), 1, .6));// body
        return hitBox;
    }

    public static EntityHitBox createZombieHitBox(Zombie zombie) {
        Entity entity = zombie.getBukkitZombie();
        World world = entity.getWorld();
        EntityHitBox hitBox = new EntityHitBox(entity, zombie.getLength(), zombie.getWidth(), zombie.getHeight());
        Location from = hitBox.getFrom().toLocation(world);
        Location to = hitBox.getTo().toLocation(world);
        double boxX = Math.min(from.getX(), to.getX());
        double boxY = Math.min(from.getY(), to.getY());
        double boxZ = Math.min(from.getZ(), to.getZ());
        hitBox.addZone(new DataZone(world, new Vector(boxX, boxY + 1.4, boxZ), new Vector(boxX + 1.031, boxY + 2, boxZ + .891), 2, 1.25));// head
        hitBox.addZone(new DataZone(world, new Vector(boxX, boxY, boxZ), new Vector(boxX + .891, boxY + 1.4, boxZ + 1.031), 1, 1));// body
        return hitBox;
    }

    public static void dropItemAtPlayer(final Location from, final ItemStack item, final Player player, final int dropDelay, final int removalDelay) {
        new DelayedTask(dropDelay, true) {
            @Override public void run() {
                Item i = from.getWorld().dropItem(from, item);
                i.setPickupDelay(Integer.MAX_VALUE);
                final ItemStack is = i.getItemStack();
                final Item finali = i;
                new DelayedTask(removalDelay, true) {
                    @Override public void run() {
                        finali.remove();
                        Ablockalypse.getExternal().getItemFileManager().giveItem(player, is);
                    }
                };
            }
        };
    }

    @SuppressWarnings("serial") public static List<PathfinderGoal> getGoals(final World world, final double length, final double width, final double height, final double rot) {
        return new ArrayList<PathfinderGoal>() {
            {
                add(new PathfinderGoal() {
                    @Override public PathNode transform(List<PathNode> navigated, PathNode finish, PathNode node) {
                        if (navigated.contains(node)) {
                            setFinish(navigated.get(navigated.size() - 1), finish);
                        }
                        if (!canFit(new Location(world, node.x, node.y + height - .5, node.z), length, width, height, rot)) {
                            node.H = Double.POSITIVE_INFINITY;// no travelling through blocks
                        }
                        if (world.getBlockAt(node.x, node.y - 2, node.z).isEmpty()) {
                            node.H += 3;// discourage falling, prevent flying
                        }
                        return node;
                    }

                    private boolean canFit(Location center, double length, double width, double height, double rot) {
                        double diagonal = Math.sqrt(Math.pow(length, 2) + Math.pow(width, 2));
                        double viewWidth = diagonal * Math.cos(Math.toRadians(rot));
                        double viewLength = diagonal * Math.sin(Math.toRadians(rot));
                        for (double addX = -viewWidth / 2; addX <= viewWidth / 2; addX++) {
                            for (double addY = -height / 2; addY <= height / 2; addY++) {
                                for (double addZ = -viewLength / 2; addZ <= viewLength / 2; addZ++) {
                                    if (center.clone().add(addX, addY, addZ).getBlock().getType().isSolid()) {
                                        return false;
                                    }
                                }
                            }
                        }
                        return true;
                    }

                    private void setFinish(PathNode node, PathNode finish) {
                        finish.x = node.x;
                        finish.y = node.y;
                        finish.z = node.z;
                    }
                });
            }
        };
    }

    public static RepeatingTask scheduleNearbyWarning(final Location loc, final String warning, final double xRad, final double yRad, final double zRad, final int intervalInMilliseconds) {
        return scheduleNearbyWarning(loc, warning, new ArrayList<UUID>(), xRad, yRad, zRad, intervalInMilliseconds);
    }

    public static RepeatingTask scheduleNearbyWarning(final Location loc, final String warning, final double rad, final int intervalInMilliseconds) {
        return scheduleNearbyWarning(loc, warning, rad, rad, rad, intervalInMilliseconds);
    }

    public static RepeatingTask scheduleNearbyWarning(final Location loc, final String warning, final List<UUID> avoid, final double xRad, final double yRad, final double zRad, final int intervalInMilliseconds) {
        return new RepeatingTask(intervalInMilliseconds / 2000, true) {
            @Override public void run() {
                for (Entity nearby : BukkitUtility.getNearbyEntities(loc, xRad, yRad, zRad)) {
                    if (nearby instanceof Player && data.isZAPlayer((Player) nearby)) {
                        Player player = (Player) nearby;
                        if (avoid.contains(player.getUniqueId())) {
                            continue;
                        }
                        ZAPlayer zap = data.getZAPlayer(player);
                        if (!zap.isTeleporting()) {
                            String name = player.getName();
                            if (messagesSent.containsKey(name)) {
                                long timePast = System.currentTimeMillis() - messagesSent.get(name);
                                if (timePast >= intervalInMilliseconds) {
                                    messagesSent.remove(name);
                                }
                            } else {
                                messagesSent.put(name, System.currentTimeMillis());
                                // if ((Boolean) Setting.ITEM_NAME_MESSAGES.getSetting()) {
                                // new PopupMessage(player, "Press SHIFT to teleport.", 5);
                                // } else {
                                player.sendMessage(warning);
                                // }
                            }
                        }
                    }
                }
            }
        };
    }
}
