package com.github.threading.inherent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.ZAPlayer;
import com.github.behavior.ZARepeatingTask;
import com.github.enumerated.Setting;

public class MobClearingThread implements ZARepeatingTask {
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
        data.objects.remove(this);
    }

    @Override public void run() {
        for (ZAPlayer zap : data.getObjectsOfType(ZAPlayer.class)) {
            Player p = zap.getPlayer();
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
        data.objects.add(this);
    }
}
