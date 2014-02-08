package com.github.jamesnorris.ablockalypse.threading.inherent;

import org.bukkit.entity.Player;

import com.github.jamesnorris.ablockalypse.aspect.Barrier;
import com.github.jamesnorris.ablockalypse.aspect.Game;
import com.github.jamesnorris.ablockalypse.aspect.MobSpawner;
import com.github.jamesnorris.ablockalypse.enumerated.ZAEffect;
import com.github.jamesnorris.ablockalypse.threading.DelayedTask;
import com.github.jamesnorris.ablockalypse.utility.SpawnUtility;

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
                MobSpawner spawner = SpawnUtility.getClosestSpawner(game, p, !noneActive);
                if (spawner.isActive() || noneActive) {
                    SpawnUtility.spawn(game, spawner.getBukkitLocation().clone().add(0, 2, 0), true, game.getWolfPercentage());
                    spawner.playEffect(ZAEffect.FLAMES);
                    return;
                }
            }
            if (!game.getObjectsOfType(Barrier.class).isEmpty()) {
                SpawnUtility.spawn(game, SpawnUtility.getClosestBarrier(game, p).getSpawnLocation(), true, game.getWolfPercentage());
                return;
            }
            SpawnUtility.spawn(game, p.getLocation(), false, game.getWolfPercentage());
        }
    }
}
