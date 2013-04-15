package com.github.jamesnorris.threading;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class NextLevelThread extends DataManipulator implements ZARepeatingThread {
    private Game game;
    private int counter, interval, count = 0;
    private String name;
    private boolean played, running, runThrough;

    public NextLevelThread(Game game, boolean autorun, int interval) {
        this.game = game;
        name = game.getName();
        played = false;
        counter = 3;
        running = false;
        this.interval = interval;
        if (autorun)
            setRunThrough(true);
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }

    public boolean isRunning() {
        return running;
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    @Override public void run() {
        running = true;
        if (game.isPaused())
            remove();
        if (data.gameExists(name) && game.hasStarted() && game.getMobCount() <= 0 && !game.isPaused() && game.getSpawnManager().allSpawnedIn()) {
            --counter;
            if (!played) {
                played = true;
                if (game.getLevel() != 0) {
                    for (String s : game.getPlayers()) {
                        ZAPlayer zap = data.getZAPlayer(Bukkit.getPlayer(s), game.getName(), false);
                        if (zap != null) {
                            ZASound.PREV_LEVEL.play(zap.getPlayer().getLocation());
                            Player p = zap.getPlayer();
                            p.sendMessage(ChatColor.BOLD + "Level " + ChatColor.RESET + ChatColor.RED + game.getLevel() + ChatColor.RESET + ChatColor.BOLD + " over... Next level: " + ChatColor.RED + (game.getLevel() + 1));
                        }
                    }
                    game.broadcastPoints();
                }
            }
            if (counter == 0) {
                played = false;
                if (game.isWolfRound())
                    game.setWolfRound(false);
                game.nextLevel();
                for (String s : game.getPlayers()) {
                    ZAPlayer zap = data.getZAPlayer(Bukkit.getPlayer(s), game.getName(), false);
                    if (zap != null) {
                        ZASound.NEXT_LEVEL.play(zap.getPlayer().getLocation());
                    }
                }
                remove();
            }
        }
    }

    @Override public void remove() {
        count = 0;
        running = false;
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
