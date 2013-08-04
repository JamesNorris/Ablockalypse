package com.github.threading.inherent;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Barrier;
import com.github.behavior.ZARepeatingTask;
import com.github.enumerated.ZAEffect;
import com.github.enumerated.ZASound;

public class BarrierBreakThread implements ZARepeatingTask {
    private Barrier barrier;
    private Location center;
    private int count = 0, interval;
    private DataContainer data = Ablockalypse.getData();
    private LivingEntity entity;
    private boolean runThrough = false;

    public BarrierBreakThread(Barrier barrier, LivingEntity entity, int interval, boolean autorun) {
        this.barrier = barrier;
        this.entity = entity;
        center = barrier.getCenter();
        this.interval = interval;
        runThrough = autorun;
        addToThreads();
    }

    @Override public int getCount() {
        return count;
    }

    @Override public int getInterval() {
        return interval;
    }

    @Override public void remove() {
        data.objects.remove(this);
    }

    @Override public void run() {
        if (!(!entity.isDead() && barrier.isWithinRadius(entity, 2) && !barrier.isBroken())) {
            remove();
            return;
        }
        int hittimes = barrier.getHitTimes();
        barrier.setHitTimes(--hittimes);
        ZASound.BARRIER_BREAK.play(center);
        ZAEffect.WOOD_BREAK.play(center);
        if (hittimes == 0) {
            barrier.setHitTimes(barrier.getHitRequirement());
            barrier.breakPanels();
            remove();
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

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    private synchronized void addToThreads() {
        data.objects.add(this);
    }
}
