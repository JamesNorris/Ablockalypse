package com.github.threading.inherent;

import org.bukkit.entity.Player;

import com.github.aspect.block.Barrier;
import com.github.aspect.block.MobSpawner;
import com.github.aspect.intelligent.Game;
import com.github.enumerated.ZAEffect;
import com.github.manager.SpawnManager;
import com.github.threading.DelayedTask;

public class MobSpawningTask extends DelayedTask {
    private Game game;

    public MobSpawningTask(Game game, int delay, boolean autorun) {
        super(delay, autorun);
        this.game = game;
    }

    @Override public void run() {
        if (game.getRemainingPlayers().size() >= 1 && !game.isPaused()) {
            Player p = game.getRandomLivingPlayer();
            if (!game.getObjectsOfType(MobSpawner.class).isEmpty()) {
                boolean noneActive = true;
                for (MobSpawner check : game.getObjectsOfType(MobSpawner.class)) {
                    if (check.isActive()) {
                        noneActive = false;
                        break;
                    }
                }
                MobSpawner spawner = SpawnManager.getClosestSpawner(game, p, !noneActive);
                if (spawner.isActive() || noneActive) {
                    SpawnManager.spawn(game, spawner.getBukkitLocation().clone().add(0, 2, 0), true, game.getWolfPercentage());
                    spawner.playEffect(ZAEffect.FLAMES);
                    return;
                }
            }
            if (!game.getObjectsOfType(Barrier.class).isEmpty()) {
                SpawnManager.spawn(game, SpawnManager.getClosestBarrier(game, p).getSpawnLocation(), true, game.getWolfPercentage());
                return;
            }
            SpawnManager.spawn(game, p.getLocation(), false, game.getWolfPercentage());
        }
    }
}
