package com.github.jamesnorris.threading;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class RespawnThread implements ZARepeatingThread {
    private DataContainer data = Ablockalypse.getData();
    private Player player;
    private boolean runThrough = false, messageSent = false;
    private int time, level, count = 0, interval;
    private ZAPlayer zap;

    /**
     * Creates a new RespawnThread instance.
     * 
     * @param player The player to wait for
     * @param time The time to count down
     * @param autorun Whether or not to automatically run the thread
     */
    public RespawnThread(Player player, int time, boolean autorun) {
        this.player = player;
        this.time = time;
        zap = data.getZAPlayer(player);
        level = zap.getGame().getLevel();
        runThrough = autorun;
        addToThreads();
    }

    @Override public int getCount() {
        return count;
    }

    @Override public int getInterval() {
        return interval;
    }

    @Override public void remove() {
        data.threads.remove(this);
    }

    @Override public void run() {
        if (!data.isZAPlayer(player)) {
            remove();
            return;
        }
        if (player.isDead() || !player.isOnline()) {
            return;
        }
        if (!messageSent) {
            player.sendMessage(ChatColor.GRAY + "You will respawn at the beginning of the next level.");
            messageSent = true;
        }
        if (zap.getGame().getLevel() > level) {
            if (time == 0) {
                ZAPlayer zap = data.players.get(player);
                if (zap.getGame() == null) {
                    remove();
                    return;
                }
                zap.sendToMainframe("Respawn");
                zap.setLimbo(false);
                remove();
                return;
            } else {
                player.sendMessage(ChatColor.GRAY + "Waiting to respawn... " + time);
            }
            --time;
        }
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setCount(int i) {
        count = i;
    }

    @Override public void setInterval(int i) {
        interval = i;
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }
}