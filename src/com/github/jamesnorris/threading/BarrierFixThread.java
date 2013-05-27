package com.github.jamesnorris.threading;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class BarrierFixThread implements ZARepeatingThread {
    private Barrier barrier;
    private Location center;
    private int count = 0, interval;
    private DataContainer data = Ablockalypse.getData();
    private Player player;
    private boolean runThrough = false;
    private ZAPlayer zap;

    public BarrierFixThread(Barrier barrier, ZAPlayer zap, int interval, boolean autorun) {
        this.barrier = barrier;
        this.zap = zap;
        player = zap.getPlayer();
        center = barrier.getCenter();
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
        if ((player == null || !player.isSneaking()) || player.isDead() || !barrier.isWithinRadius(player, 2) || !barrier.isBroken()) {
            remove();
            return;
        }
        int fixtimes = barrier.getFixTimes();
        barrier.setFixTimes(--fixtimes);
        if (fixtimes > 0) {
            zap.addPoints((Integer) Setting.BARRIER_PART_FIX_PAY.getSetting());
        }
        ZASound.BARRIER_REPAIR.play(center);
        ZAEffect.WOOD_BREAK.play(center);
        if (fixtimes == 0) {
            zap.addPoints((Integer) Setting.BARRIER_FULL_FIX_PAY.getSetting());
            barrier.setFixTimes(barrier.getFixRequirement());
            barrier.replacePanels();
            remove();
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
