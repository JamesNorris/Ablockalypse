package com.github.jamesnorris.threading;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Claymore;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class ClaymoreTriggerThread implements ZARepeatingThread {
    private DataContainer data = DataContainer.data;
    private boolean runThrough = false;
    private int count = 0, interval;
    private Claymore claymore;

    public ClaymoreTriggerThread(Claymore claymore, int interval, boolean autorun) {
        this.claymore = claymore;
        this.interval = interval;
        if (autorun)
            setRunThrough(true);
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }

    @Override public void run() {
        if (claymore == null || !claymore.getGame().hasStarted()) {
            remove();
        } else {
            for (ZAMob mob : claymore.getGame().getMobs()) {
                if (claymore.isWithinExplosionDistance(mob.getEntity().getLocation())) {
                    claymore.trigger();
                    remove();
                }
            }
        }
    }

    @Override public void remove() {
        data.threads.remove(this);
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
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
