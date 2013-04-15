package com.github.jamesnorris.threading;

import org.bukkit.entity.LivingEntity;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class BarrierBreakTriggerThread extends DataManipulator implements ZARepeatingThread {
    private boolean runThrough = false;
    private int count = 0, interval;

    public BarrierBreakTriggerThread(boolean autorun, int interval) {
        if (autorun)
            setRunThrough(true);
        this.interval = interval;
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
        for (Barrier bg : data.barriers) {
            for (ZAMob mob : data.mobs) {
                if (bg.isWithinRadius(mob.getEntity()) && !bg.isBroken()) {
                    bg.breakBarrier((LivingEntity) mob.getEntity());
                }
            }
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
