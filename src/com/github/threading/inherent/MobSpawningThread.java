package com.github.threading.inherent;

import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Barrier;
import com.github.aspect.Game;
import com.github.aspect.MobSpawner;
import com.github.behavior.ZADelayedTask;
import com.github.enumerated.ZAEffect;
import com.github.manager.SpawnManager;

public class MobSpawningThread implements ZADelayedTask {
    private DataContainer data = Ablockalypse.getData();
    private int delay, countup = 0;
    private Game game;

    public MobSpawningThread(Game game, int delay) {
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
        data.objects.remove(this);
    }

    @Override public void run() {
        if (game.getRemainingPlayers() >= 1 && !game.isPaused()) {
            Player p = game.getRandomLivingPlayer();
            if (!game.getObjectsOfType(MobSpawner.class).isEmpty()) {
                MobSpawner spawner = SpawnManager.getClosestSpawner(game, p);
                if (spawner.isActive()) {
                    game.spawn(spawner.getBukkitLocation().clone().add(0, 2, 0), true);
                    spawner.playEffect(ZAEffect.FLAMES);
                    return;
                }
            }
            if (!game.getObjectsOfType(Barrier.class).isEmpty()) {
                game.spawn(SpawnManager.getClosestBarrier(game, p).getSpawnLocation(), true);
                return;
            }
            game.spawn(p.getLocation(), false);
        }
    }

    @Override public void setCountup(int countup) {
        this.countup = countup;
    }

    private synchronized void addToThreads() {
        data.objects.add(this);
    }
}
