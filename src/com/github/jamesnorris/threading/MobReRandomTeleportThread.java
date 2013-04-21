package com.github.jamesnorris.threading;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class MobReRandomTeleportThread implements ZARepeatingThread {
    private DataContainer data = DataContainer.data;
    private boolean runThrough = false;
    private int count = 0, interval;
    private Creature c;
    private Game game;

    public MobReRandomTeleportThread(Creature c, Game game, boolean autorun, int interval) {
        setRunThrough(autorun);
        this.interval = interval;
        this.c = c;
        this.game = game;
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
        if (!c.isDead() && data.isZAMob((Entity) c)) {
            Location target = game.getRandomLivingPlayer().getLocation();
            Location strike = game.getSpawnManager().findSpawnLocation(target, 5, 3);
            c.teleport(strike);
            strike.getWorld().strikeLightning(strike);
        } else {
            remove();
        }
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
