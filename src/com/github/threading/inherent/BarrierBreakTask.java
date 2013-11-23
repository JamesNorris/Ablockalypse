package com.github.threading.inherent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.block.Barrier;
import com.github.enumerated.ZAEffect;
import com.github.enumerated.ZASound;
import com.github.threading.RepeatingTask;

public class BarrierBreakTask extends RepeatingTask {
    private static final List<UUID> breakers = new ArrayList<UUID>();
    private static final int INTERVAL = 100;
    private Barrier barrier;
    private Location center;
    private DataContainer data = Ablockalypse.getData();
    private LivingEntity entity;

    public BarrierBreakTask(Barrier barrier, LivingEntity entity, boolean autorun) {
        super(INTERVAL, autorun);
        UUID entityID = entity.getUniqueId();
        if (breakers.contains(entityID)) {
            return;
        }
        breakers.add(entityID);
        this.barrier = barrier;
        this.entity = entity;
        center = barrier.getCenter();
    }

    @Override public void cancel() {
        if (entity != null) {
            breakers.remove(entity.getUniqueId());
        }
        data.objects.remove(this);
    }

    @Override public void run() {
        if (entity == null || barrier == null || entity.isDead() || !(barrier.getCenter().distanceSquared(entity.getLocation()) < 4) || barrier.isBroken()) {
            cancel();
            return;
        }
        barrier.setHP(barrier.getHP() - 1);
        ZASound.BARRIER_BREAK.play(center);
        ZAEffect.WOOD_BREAK.play(center);
        if (barrier.getHP() == 0) {
            if (data.isZAMob(entity)) {
                data.getZAMob(entity).retarget();
            }
            barrier.breakPanels();
            cancel();
        }
    }
}
