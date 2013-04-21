package com.github.jamesnorris.threading;

import org.bukkit.entity.Player;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.MobSpawner;
import com.github.jamesnorris.inter.ZADelayedThread;
import com.github.jamesnorris.manager.SpawnManager;

public class MobSpawningThread implements ZADelayedThread {
    private DataContainer data = DataContainer.data;
    private int delay, countup = 0;
    private Game game;
    private SpawnManager sm;

    public MobSpawningThread(SpawnManager sm, Game game, int delay) {
        this.sm = sm;
        this.game = game;
        this.delay = delay;
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }

    @Override public void run() {
        if (game.getRemainingPlayers() >= 1 && !game.isPaused()) {
            Player p = game.getRandomLivingPlayer();
            if (game.getObjectsOfType(MobSpawner.class).size() > 0) {
                MobSpawner zaloc = sm.getClosestSpawner(p);
                game.spawn(zaloc.getBukkitLocation().clone().add(0, 2, 0), true);
                zaloc.playEffect(ZAEffect.FLAMES);
                zaloc.getBukkitLocation().clone().subtract(0, 2, 0);
            } else if (game.getObjectsOfType(Barrier.class).size() > 0) {
                game.spawn(sm.getClosestBarrier(p).getSpawnLocation(), true);
            } else {
                game.spawn(p.getLocation(), false);
            }
        }
    }

    @Override public void remove() {
        data.threads.remove(this);
    }

    @Override public int getDelay() {
        return delay;
    }

    @Override public int getCountup() {
        return countup;
    }

    @Override public void setCountup(int countup) {
        this.countup = countup;
    }
}
