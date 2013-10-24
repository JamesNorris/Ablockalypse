package com.github.utility;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.entity.Hellhound;
import com.github.aspect.entity.ZAPlayer;
import com.github.aspect.entity.Zombie;
import com.github.threading.RepeatingTask;
import com.github.utility.ranged.DataZone;
import com.github.utility.ranged.type.EntityHitBox;

public class AblockalypseUtility {
    private static DataContainer data = Ablockalypse.getData();
    private static HashMap<String, Long> messagesSent = new HashMap<String, Long>();

    public static RepeatingTask scheduleNearbyWarning(final Location loc, final String warning, final double xRad, final double yRad, final double zRad, final int intervalInMilliseconds) {
        return new RepeatingTask((int) (intervalInMilliseconds / 2000), true) {
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
    
    //TODO are the hitbox methods accurate in dimensions?
    public static EntityHitBox createHellhoundHitBox(Hellhound hound) {
        try {
            Entity entity = hound.getEntity();
            EntityHitBox hitBox = new EntityHitBox(entity, 1.594D, .5D, .969D);
            hitBox.addDataZone(new DataZone(0F, .5F, .45F, .969F, .1F, .4F, 2D, .75D));//head
            hitBox.addDataZone(new DataZone(.4F, 1.594F, 0F, .969F, 0F, .5F, 1D, .6D));//body
            return hitBox;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static EntityHitBox createZombieHitBox(Zombie zombie) {
        try {
            Entity entity = zombie.getEntity();
            EntityHitBox hitBox = new EntityHitBox(entity, .891, 1.031D, 2D);
            hitBox.addDataZone(new DataZone(0F, 1.031F, 1.4F, 2F, 0F, .891F, 2D, 1.25D));//head
            hitBox.addDataZone(new DataZone(0F, .891F, 0F, 1.4F, 0F, 1.031F, 1D, 1D));//body
            return hitBox;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
