package com.github.threading.inherent;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Barrier;
import com.github.aspect.ZAPlayer;
import com.github.behavior.ZARepeatingTask;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.enumerated.ZASound;

public class BarrierFixThread implements ZARepeatingTask {
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
        data.objects.remove(this);
    }

    @Override public void run() {
        if (player == null || !player.isSneaking() || player.isDead() || !barrier.isWithinRadius(player, 2) || !barrier.isBroken()) {
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
        data.objects.add(this);
    }
}
