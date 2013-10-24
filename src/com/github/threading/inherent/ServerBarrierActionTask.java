package com.github.threading.inherent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.block.Barrier;
import com.github.threading.RepeatingTask;
import com.github.utility.BukkitUtility;

public class ServerBarrierActionTask extends RepeatingTask {
    private static final int INTERVAL = 20;
    private DataContainer data = Ablockalypse.getData();

    public ServerBarrierActionTask(boolean autorun) {
        super(INTERVAL, autorun);
    }

    @Override public void run() {
        for (Barrier bg : data.getObjectsOfType(Barrier.class)) {
            for (Entity nearby : BukkitUtility.getNearbyEntities(bg.getCenter(), 2, 3, 2)) {
                if (!(nearby instanceof LivingEntity)) {
                    continue;
                }
                if (data.isZAMob((LivingEntity) nearby)) {
                    if (bg.getCenter().distanceSquared(nearby.getLocation()) < 4 && !bg.isBroken()) {
                        bg.breakBarrier((LivingEntity) nearby);// will not let the mob fix if it is already
                    }
                }
            }
        }
    }
}
