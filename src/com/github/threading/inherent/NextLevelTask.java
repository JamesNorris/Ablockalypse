package com.github.threading.inherent;

import org.bukkit.ChatColor;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.intelligent.Game;
import com.github.behavior.GameObject;
import com.github.enumerated.Setting;
import com.github.enumerated.ZASound;
import com.github.manager.SpawnManager;
import com.github.threading.RepeatingTask;

public class NextLevelTask extends RepeatingTask {
    private static final int INTERVAL = 20;
    private int counter;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private String name;
    private boolean played;

    public NextLevelTask(Game game, boolean autorun) {
        super(INTERVAL, autorun);
        this.game = game;
        name = game.getName();
        played = false;
        counter = (Integer) Setting.LEVEL_TRANSITION_TIME.getSetting();
        if (counter < 20) {
            counter = 20;
        }
    }

    @Override public void run() {
        if (game.isPaused()) {
            cancel();
            return;
        }
        if (data.gameExists(name) && game.hasStarted() && game.getMobCount() <= 0 && SpawnManager.allSpawnedIn(game)) {
            if (!played) {
                played = true;
                if (game.getLevel() != 0) {
                    for (GameObject obj : game.getObjects()) {
                        obj.onLevelEnd();
                    }
                }
            }
            --counter;
            if (counter <= 5) {
                game.broadcast("" + ChatColor.RED + counter + ChatColor.RESET + ChatColor.BOLD + " seconds left.");
            }
            if (counter == 0) {
                played = false;
                if (game.isWolfRound()) {
                    game.setWolfRound(false);
                }
                game.nextLevel();
                game.broadcastSound(ZASound.NEXT_LEVEL);
                cancel();
            }
        }
    }
}
