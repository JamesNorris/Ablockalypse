package com.github.aspect.intelligent;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.entity.ZAPlayer;
import com.github.behavior.GameObject;
import com.github.enumerated.Setting;
import com.github.threading.RepeatingTask;

public class GameScoreboard implements GameObject {
    private Game game;
    private Scoreboard board;
    private Objective points, health;
    private DataContainer data = Ablockalypse.getData();

    public GameScoreboard(Game game) {
        this.game = game;
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        points = board.registerNewObjective("points", "dummy");
        points.setDisplaySlot(DisplaySlot.SIDEBAR);
        points.setDisplayName(game.getName() + " - " + game.getLevel());
        if ((Boolean) Setting.DISPLAY_PLAYER_HEALTH.getSetting()) {
            health = board.registerNewObjective("health", "health");
            health.setDisplaySlot(DisplaySlot.BELOW_NAME);
            health.setDisplayName("/ 20");
        }
        startScan();
        game.addObject(this);
        data.objects.add(this);
    }

    @Override public Game getGame() {
        return game;
    }

    public void startScan() {
        new RepeatingTask(20, true) {
            @Override public void run() {
                for (OfflinePlayer offline : board.getPlayers()) {
                    if (offline.isOnline()) {
                        Player player = offline.getPlayer();
                        if (data.isZAPlayer(player)) {
                            ZAPlayer zap = data.getZAPlayer(player);
                            if (zap.getGame() != game) {
                                removePlayerScoreboard(player);
                            }
                        } else {
                            removePlayerScoreboard(player);
                        }
                    }
                }
                for (ZAPlayer zap : game.getPlayers()) {
                    Player player = zap.getPlayer();
                    boolean unLoaded = false;
                    if (player.getScoreboard() != board) {
                        player.setScoreboard(board);
                        unLoaded = true;
                    }
                    Score score = points.getScore(player);
                    if (unLoaded) {
                        score.setScore(-1);
                    }
                    score.setScore(zap.getPoints());
                }
                points.setDisplayName(game.getName() + " - " + game.getLevel());
            }

            private void removePlayerScoreboard(Player player) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        };
    }

    @Override public void remove() {
        game.removeObject(this);
        data.objects.remove(this);
    }//board should be removed onDisable

    @Override public void onGameEnd() {}

    @Override public void onGameStart() {}

    @Override public void onNextLevel() {}

    @Override public void onLevelEnd() {}

    @Override public Block getDefiningBlock() {
        return null;
    }

    @Override public ArrayList<Block> getDefiningBlocks() {
        return null;
    }

    @Override public int getLoadPriority() {
        return 3;
    }
}
