package com.github.jamesnorris.threading;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class NextLevelThread implements ZARepeatingThread {
    private int counter, interval = 20, count = 0;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private String name;
    private boolean played, running, runThrough;

    public NextLevelThread(Game game, boolean autorun) {
        this.game = game;
        name = game.getName();
        played = false;
        counter = (Integer) Setting.LEVEL_TRANSITION_TIME.getSetting();
        if (counter < 20) {
            counter = 20;
        }
        running = false;
        if (autorun) {
            setRunThrough(true);
        }
        addToThreads();
    }

    @Override public int getCount() {
        return count;
    }

    @Override public int getInterval() {
        return interval;
    }

    public boolean isRunning() {
        return running;
    }

    @Override public void remove() {
        count = 0;
        running = false;
        data.threads.remove(this);
    }

    @Override public void run() {
        running = true;
        if (game.isPaused()) {
            remove();
            return;
        }
        if (data.gameExists(name) && game.hasStarted() && game.getMobCount() <= 0 && game.getSpawnManager().allSpawnedIn()) {
            if (!played) {
                played = true;
                if (game.getLevel() != 0) {
                    for (String s : game.getPlayers()) {
                        ZAPlayer zap = data.getZAPlayer(Bukkit.getPlayer(s), game.getName(), false);
                        if (zap != null) {
                            ZASound.PREV_LEVEL.play(zap.getPlayer().getLocation());
                            Player p = zap.getPlayer();
                            //@formatter:off
                            p.sendMessage(ChatColor.BOLD + "Level " + ChatColor.RESET + ChatColor.RED + game.getLevel() + ChatColor.RESET + ChatColor.BOLD 
                                    + " over... Next level: " + ChatColor.RED + (game.getLevel() + 1) + "\n" + ChatColor.RESET + ChatColor.BOLD + "Time to next level: "
                                    + ChatColor.RED + (Integer) Setting.LEVEL_TRANSITION_TIME.getSetting() + ChatColor.RESET + ChatColor.BOLD + " seconds.");
                            //@formatter:on
                        }
                    }
                    game.broadcastPoints();
                }
            }
            --counter;
            if (counter <= 5) {
                for (String s : game.getPlayers()) {
                    Bukkit.getPlayer(s).sendMessage("" + ChatColor.RED + counter + ChatColor.RESET + ChatColor.BOLD + " seconds left.");
                }
            }
            if (counter == 0) {
                played = false;
                if (game.isWolfRound()) {
                    game.setWolfRound(false);
                }
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

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setCount(int i) {
        count = i;
    }

    @Override public void setInterval(int i) {
        //don't! it will break!
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }
}
