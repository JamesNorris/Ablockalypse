package com.github.jamesnorris.threading;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Hellhound;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class HellhoundMaintenanceThread implements ZARepeatingThread {
    private DataContainer data = DataContainer.data;
    private boolean runThrough = false;
    private int count = 0, interval;

    public HellhoundMaintenanceThread(boolean autorun, int interval) {
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
        if (data.hellhounds != null) {
            for (Hellhound f : data.hellhounds) {
                if (!f.getWolf().isDead()) {
                    f.addFlames();
                    f.setAggressive(true);
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
