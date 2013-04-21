package com.github.jamesnorris.threading;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class RespawnThread implements ZARepeatingThread {
    private DataContainer data = DataContainer.data;
    private Player player;
    private int time, level, count = 0, interval;
    private ZAPlayer zap;
    private boolean runThrough = false, messageSent = false;

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
        if (autorun)
            setRunThrough(true);
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
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

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    @Override public void run() {
        if (!messageSent) {
            player.sendMessage(ChatColor.GRAY + "You will respawn at the beginning of the next level.");
            messageSent = true;
        }
        if (data.playerExists(player)) {
            if (zap.getGame().getLevel() > level) {
                if (time == 0) {
                    ZAPlayer zap = data.players.get(player);
                    if (zap.getGame() == null)
                        remove();
                    zap.sendToMainframe("Respawn");
                    zap.setLimbo(false);
                    remove();
                } else
                    player.sendMessage(ChatColor.GRAY + "Waiting to respawn... " + time);
                --time;
            }
        } else
            remove();
    }

    @Override public void remove() {
        data.threads.remove(this);
    }
}
