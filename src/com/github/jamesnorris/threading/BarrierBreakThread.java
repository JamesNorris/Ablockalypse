package com.github.jamesnorris.threading;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class BarrierBreakThread extends DataManipulator implements ZARepeatingThread {
    private boolean runThrough = false;
    private int count = 0, interval;
    private Barrier barrier;
    private LivingEntity entity;
    private Location center;

    public BarrierBreakThread(Barrier barrier, LivingEntity entity, int interval, boolean autorun) {
        this.barrier = barrier;
        this.entity = entity;
        this.center = barrier.getCenter();
        this.interval = interval;
        if (autorun)
            setRunThrough(true);
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    @Override public void run() {
        if (!entity.isDead() && barrier.isWithinRadius(entity) && !barrier.isBroken()) {
            int hittimes = barrier.getHitTimes();
            barrier.setHitTimes(--hittimes);
            ZASound.BARRIER_BREAK.play(center);
            ZAEffect.WOOD_BREAK.play(center);
            if (hittimes == 0) {
                barrier.setHitTimes(barrier.getHitRequirement());
                barrier.breakPanels();
                remove();
            }
        } else {
            remove();
        }
    }

    @Override public void remove() {
        data.threads.remove(this);
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
