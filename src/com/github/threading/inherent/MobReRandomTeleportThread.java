package com.github.threading.inherent;

import org.bukkit.Location;
import org.bukkit.entity.Creature;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Game;
import com.github.behavior.ZARepeatingTask;
import com.github.utility.MiscUtil;

public class MobReRandomTeleportThread implements ZARepeatingTask {
    private Creature c;
    private int count = 0, interval;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private boolean runThrough = false;

    public MobReRandomTeleportThread(Creature c, Game game, boolean autorun, int interval) {
        runThrough = autorun;
        this.interval = interval;
        this.c = c;
        this.game = game;
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
        if (!(!c.isDead() && data.isZAMob(c))) {
            remove();
            return;
        }
        Location target = game.getRandomLivingPlayer().getLocation();
        Location strike = MiscUtil.getNearbyLocation(target, 2, 4, 0, 0, 2, 4);
        c.teleport(strike);
        strike.getWorld().strikeLightning(strike);
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
