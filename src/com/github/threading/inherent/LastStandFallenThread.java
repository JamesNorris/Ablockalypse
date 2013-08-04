package com.github.threading.inherent;

import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.ZAPlayer;
import com.github.behavior.ZARepeatingTask;

public class LastStandFallenThread implements ZARepeatingTask {
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
        data.objects.remove(this);
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
        data.objects.add(this);
    }
}
