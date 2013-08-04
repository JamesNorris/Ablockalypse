package com.github.threading.inherent;

import org.bukkit.Location;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Mainframe;
import com.github.behavior.ZARepeatingTask;
import com.github.enumerated.ZAEffect;

public class LinkedTeleporterEffectThread implements ZARepeatingTask {
    private DataContainer data = Ablockalypse.getData();
    private ZAEffect[] effects;
    private Mainframe frame;
    private int interval, count = 0;
    private boolean runThrough = false;

    public LinkedTeleporterEffectThread(Mainframe frame, int interval, ZAEffect[] effects, boolean autorun) {
        this.frame = frame;
        this.interval = interval;
        this.effects = effects;
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
        for (ZAEffect effect : effects) {
            for (Location link : frame.getLinks()) {
                for (int i = 1; i <= 2; i++) {
                    effect.play(link.clone().add(0, i, 0));
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
