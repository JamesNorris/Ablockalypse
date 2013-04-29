package com.github.jamesnorris.threading;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Hellhound;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class HellhoundMaintenanceThread implements ZARepeatingThread {
    private int count = 0, interval;
    private DataContainer data = Ablockalypse.getData();
    private boolean runThrough = false;

    public HellhoundMaintenanceThread(boolean autorun, int interval) {
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
        if (data.hellhounds != null) {
            for (Hellhound f : data.hellhounds) {
                if (!f.getWolf().isDead()) {
                    f.addFlames();
                    f.setAggressive(true);
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
