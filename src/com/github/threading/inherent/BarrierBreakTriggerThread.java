package com.github.threading.inherent;

import org.bukkit.entity.LivingEntity;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Barrier;
import com.github.behavior.ZAMob;
import com.github.behavior.ZARepeatingTask;

public class BarrierBreakTriggerThread implements ZARepeatingTask {
    private int count = 0, interval;
    private DataContainer data = Ablockalypse.getData();
    private boolean runThrough = false;

    public BarrierBreakTriggerThread(boolean autorun, int interval) {
        runThrough = autorun;
        this.interval = interval;
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
        for (Barrier bg : data.getObjectsOfType(Barrier.class)) {
            for (ZAMob mob : data.getObjectsOfType(ZAMob.class)) {
                if (bg.isWithinRadius(mob.getEntity(), 2) && !bg.isBroken()) {
                    bg.breakBarrier((LivingEntity) mob.getEntity());
                }
            }
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
