package com.github.threading.inherent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Claymore;
import com.github.behavior.ZAMob;
import com.github.behavior.ZARepeatingTask;

public class ClaymoreTriggerThread implements ZARepeatingTask {
    private Claymore claymore;
    private int count = 0, interval;
    private DataContainer data = Ablockalypse.getData();
    private boolean runThrough = false;

    public ClaymoreTriggerThread(Claymore claymore, int interval, boolean autorun) {
        this.claymore = claymore;
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
        if (claymore == null || !claymore.getGame().hasStarted()) {
            remove();
            return;
        }
        for (ZAMob mob : claymore.getGame().getMobs()) {
            if (claymore.isWithinExplosionDistance(mob.getEntity().getLocation())) {
                claymore.trigger();
                remove();
                return;
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
