package com.github.jamesnorris.threading;

import org.bukkit.entity.LivingEntity;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class BarrierBreakTriggerThread implements ZARepeatingThread {
    private int count = 0, interval;
    private DataContainer data = Ablockalypse.getData();
    private boolean runThrough = false;

    public BarrierBreakTriggerThread(boolean autorun, int interval) {
        if (autorun) {
            setRunThrough(true);
        }
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
        data.threads.remove(this);
    }

    @Override public void run() {
        for (Barrier bg : data.barriers) {
            for (ZAMob mob : data.mobs) {
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
        data.threads.add(this);
    }
}
