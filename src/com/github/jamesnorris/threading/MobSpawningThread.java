package com.github.jamesnorris.threading;

import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.MobSpawner;
import com.github.jamesnorris.inter.ZADelayedThread;
import com.github.jamesnorris.manager.SpawnManager;

public class MobSpawningThread implements ZADelayedThread {
    private DataContainer data = Ablockalypse.getData();
    private int delay, countup = 0;
    private Game game;
    private SpawnManager sm;

    public MobSpawningThread(SpawnManager sm, Game game, int delay) {
        this.sm = sm;
        this.game = game;
        this.delay = delay;
        addToThreads();
    }

    @Override public int getCountup() {
        return countup;
    }

    @Override public int getDelay() {
        return delay;
    }

    @Override public void remove() {
        data.threads.remove(this);
    }

    @Override public void run() {
        if (game.getRemainingPlayers() >= 1 && !game.isPaused()) {
            Player p = game.getRandomLivingPlayer();
            if (!game.getObjectsOfType(MobSpawner.class).isEmpty()) {
                MobSpawner zaloc = sm.getClosestSpawner(p);
                game.spawn(zaloc.getBukkitLocation().clone().add(0, 2, 0), true);
                zaloc.playEffect(ZAEffect.FLAMES);
            } else if (!game.getObjectsOfType(Barrier.class).isEmpty()) {
                game.spawn(sm.getClosestBarrier(p).getSpawnLocation(), true);
            } else {
                game.spawn(p.getLocation(), false);
            }
            sm.spawnedIn++;
        }
    }

    @Override public void setCountup(int countup) {
        this.countup = countup;
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }
}
