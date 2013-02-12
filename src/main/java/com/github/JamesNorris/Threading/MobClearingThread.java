package com.github.JamesNorris.Threading;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Interface.ZAThread;

public class MobClearingThread extends DataManipulator implements ZAThread {
    private boolean runThrough = false;
    private int count = 0, interval;

    public MobClearingThread(boolean autorun, int interval) {
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
        for (Player p : data.players.keySet())
            for (Entity e : p.getNearbyEntities(32, 32, 32))
                if (e != null && (e.getType() == EntityType.SLIME || !data.isZAMob(e)) && !(e instanceof Player))
                    e.remove();
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
