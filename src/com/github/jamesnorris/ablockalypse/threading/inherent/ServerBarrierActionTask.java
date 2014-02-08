package com.github.jamesnorris.ablockalypse.threading.inherent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.Barrier;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;
import com.github.jamesnorris.ablockalypse.utility.BukkitUtility;

public class ServerBarrierActionTask extends RepeatingTask {
    private static final int INTERVAL = 20;
    private DataContainer data = Ablockalypse.getData();

    public ServerBarrierActionTask(boolean autorun) {
        super(INTERVAL, autorun);
    }

    @Override public void run() {
        for (Barrier bg : data.getObjectsOfType(Barrier.class)) {
            if (bg.getCenter() == null) {
                continue;
            }
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
