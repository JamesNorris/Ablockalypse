package com.github.threading.inherent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.entity.ZAPlayer;
import com.github.enumerated.Setting;
import com.github.threading.RepeatingTask;

public class ServerMobClearingTask extends RepeatingTask {
    private DataContainer data = Ablockalypse.getData();

    public ServerMobClearingTask(int interval, boolean autorun) {
        super(interval, autorun);
    }

    @Override public void run() {
        for (ZAPlayer zap : data.getObjectsOfType(ZAPlayer.class)) {
            Player p = zap.getPlayer();
            int radius = (Integer) Setting.CLEAR_MOBS_RADIUS.getSetting();
            for (Entity e : p.getNearbyEntities(radius, radius, radius)) {
                if (!(e instanceof LivingEntity)) {
                    continue;
                }
                if (e != null && !data.isZAMob((LivingEntity) e) && !(e instanceof Player)) {
                    if (e instanceof LivingEntity) {
                        ((LivingEntity) e).setHealth(0);
                    } else {
                        e.remove();
                    }
                }
            }
        }
    }
}
