package com.github.jamesnorris.threading;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class MobClearingThread implements ZARepeatingThread {
    private int count = 0, interval;
    private DataContainer data = Ablockalypse.getData();
    private boolean runThrough = false;

    public MobClearingThread(boolean autorun, int interval) {
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
        data.threads.remove(this);
    }

    @Override public void run() {
        for (Player p : data.players.keySet()) {
            int radius = (Integer) Setting.CLEAR_MOBS_RADIUS.getSetting();
            for (Entity e : p.getNearbyEntities(radius, radius, radius)) {
                if (e != null && !data.isZAMob(e) && !(e instanceof Player)) {
                    e.remove();
                    return;
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
