package com.github.JamesNorris.Threading;

import org.bukkit.entity.Player;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Interface.ZAThread;

public class LastStandThread extends DataManipulator implements ZAThread {
    private Player player;
    private ZAPlayer zap;
    private boolean runThrough;
    private int interval, count = 0;

    /**
     * Creates a new LastStandThread instance.
     * 
     * @param zap The ZAPlayer to hurt
     * @param autorun
     */
    public LastStandThread(ZAPlayer zap, boolean autorun) {
        this.zap = zap;
        this.interval = 140;
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
