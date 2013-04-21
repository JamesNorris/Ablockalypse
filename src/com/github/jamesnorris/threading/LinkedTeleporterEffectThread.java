package com.github.jamesnorris.threading;

import org.bukkit.Location;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class LinkedTeleporterEffectThread implements ZARepeatingThread {
    private DataContainer data = DataContainer.data;
    private Mainframe frame;
    private ZAEffect[] effects;
    private boolean runThrough = false;
    private int interval, count = 0;

    public LinkedTeleporterEffectThread(Mainframe frame, int interval, ZAEffect[] effects, boolean autorun) {
        this.frame = frame;
        this.interval = interval;
        this.effects = effects;
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
        this.runThrough = tf;
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

    @Override public void run() {
        for (ZAEffect effect : effects) {
            for (Location link : frame.getLinks()) {
                for (int i = 1; i <= 2; i++) {
                    effect.play(link.clone().add(0, i, 0));
                }
            }
        }
    }
}
