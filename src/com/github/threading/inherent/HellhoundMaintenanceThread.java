package com.github.threading.inherent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Hellhound;
import com.github.behavior.ZARepeatingTask;
import com.github.enumerated.ZAEffect;

public class HellhoundMaintenanceThread implements ZARepeatingTask {
    private int count = 0, interval;
    private DataContainer data = Ablockalypse.getData();
    private boolean runThrough = false;

    public HellhoundMaintenanceThread(boolean autorun, int interval) {
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
        for (Hellhound f : data.getObjectsOfType(Hellhound.class)) {
            if (!f.getWolf().isDead()) {
                ZAEffect.FLAMES.play(f.getWolf().getLocation());
                f.setAggressive(true);
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
