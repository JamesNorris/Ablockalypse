package com.github.jamesnorris.ablockalypse.utility;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.entity.Hellhound;
import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;
import com.github.jamesnorris.ablockalypse.aspect.entity.Zombie;
import com.github.jamesnorris.ablockalypse.threading.DelayedTask;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;
import com.github.jamesnorris.mcshot.DataZone;
import com.github.jamesnorris.mcshot.type.EntityHitBox;

public class AblockalypseUtility {
    private static DataContainer data = Ablockalypse.getData();
    private static HashMap<String, Long> messagesSent = new HashMap<String, Long>();

    // TODO are the hitbox methods accurate in dimensions?
    public static EntityHitBox createHellhoundHitBox(Hellhound hound) {
        Entity entity = hound.getEntity();
        EntityHitBox hitBox = new EntityHitBox(entity, 1.594D, .5D, .969D);
        hitBox.addDataZone(new DataZone(0F, .5F, .45F, .969F, .1F, .4F, 2D, .75D));// head
        hitBox.addDataZone(new DataZone(.4F, 1.594F, 0F, .969F, 0F, .5F, 1D, .6D));// body
        return hitBox;
    }

    public static EntityHitBox createZombieHitBox(Zombie zombie) {
        Entity entity = zombie.getEntity();
        EntityHitBox hitBox = new EntityHitBox(entity, .891, 1.031D, 2D);
        hitBox.addDataZone(new DataZone(0F, 1.031F, 1.4F, 2F, 0F, .891F, 2D, 1.25D));// head
        hitBox.addDataZone(new DataZone(0F, .891F, 0F, 1.4F, 0F, 1.031F, 1D, 1D));// body
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

    public static OfflinePlayer forceObtainPlayer(String name) {
        OfflinePlayer player = Bukkit.getPlayer(name);
        if (player == null) {
            return Bukkit.getOfflinePlayer(name);
        }
        if (player == null || !player.hasPlayedBefore()) {
            // npes will be thrown... player doesnt exist and never did (why was it saved?)
            Ablockalypse.getErrorTracker().crash("A ZAPlayer that was loaded has no corresponding player, therefore it will cause null pointers if not stopped!", 100);
            return null;
        }
        return player;
    }

    public static RepeatingTask scheduleNearbyWarning(final Location loc, final String warning, final double xRad, final double yRad, final double zRad, final int intervalInMilliseconds) {
        return new RepeatingTask(intervalInMilliseconds / 2000, true) {
            @Override public void run() {
                for (Entity nearby : BukkitUtility.getNearbyEntities(loc, xRad, yRad, zRad)) {
                    if (nearby instanceof Player && data.isZAPlayer((Player) nearby)) {
                        Player player = (Player) nearby;
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
