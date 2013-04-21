package com.github.jamesnorris.threading;

import org.bukkit.entity.Player;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class LastStandFallenThread implements ZARepeatingThread {
    private DataContainer data = DataContainer.data;
    private Player player;
    private ZAPlayer zap;
    private boolean runThrough;
    private int interval, count = 0;

    /**
     * Creates a new LastStandThread instance.
     * 
     * @param zap The ZAPlayer to hurt
     * @param interval The interval between damage to the player
     * @param autorun Whether or not the thread will automatically run
     */
    public LastStandFallenThread(ZAPlayer zap, int interval, boolean autorun) {
        this.zap = zap;
        this.interval = interval;
        player = zap.getPlayer();
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

    @Override public void run() {
        if (zap.isInLastStand() && !player.isDead())
            player.damage(1);
        else
            remove();
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
