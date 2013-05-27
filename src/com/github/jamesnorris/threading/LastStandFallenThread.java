package com.github.jamesnorris.threading;

import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class LastStandFallenThread implements ZARepeatingThread {
    private DataContainer data = Ablockalypse.getData();
    private int interval, count = 0;
    private Player player;
    private boolean runThrough;
    private ZAPlayer zap;

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
        if (!zap.isInLastStand() || player.isDead()) {
            remove();
            return;
        }
        player.damage(1);
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
