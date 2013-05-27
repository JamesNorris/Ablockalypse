package com.github.jamesnorris.threading;

import org.bukkit.Location;
import org.bukkit.entity.Creature;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.inter.ZARepeatingThread;
import com.github.jamesnorris.manager.SpawnManager;

public class MobReRandomTeleportThread implements ZARepeatingThread {
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
        data.threads.remove(this);
    }

    @Override public void run() {
        if (!(!c.isDead() && data.isZAMob(c))) {
            remove();
            return;
        }
        Location target = game.getRandomLivingPlayer().getLocation();
        Location strike = SpawnManager.findSpawnLocation(target, 3, 5);
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
        data.threads.add(this);
    }
}
