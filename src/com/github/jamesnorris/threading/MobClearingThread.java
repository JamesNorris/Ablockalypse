package com.github.jamesnorris.threading;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class MobClearingThread implements ZARepeatingThread {
    private DataContainer data = DataContainer.data;
    private boolean runThrough = false;
    private int count = 0, interval;

    public MobClearingThread(boolean autorun, int interval) {
        this.interval = interval;
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
        runThrough = tf;
    }

    @Override public void run() {
        for (Player p : data.players.keySet()) {
            int radius = (Integer) Setting.CLEAR_MOBS_RADIUS.getSetting();
            for (Entity e : p.getNearbyEntities(radius, radius, radius)) {
                if (e != null && !data.isZAMob(e) && !(e instanceof Player)) {
                    e.remove();
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
