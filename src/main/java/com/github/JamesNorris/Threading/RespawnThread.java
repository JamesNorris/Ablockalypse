package com.github.JamesNorris.Threading;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Interface.ZAThread;
import com.github.JamesNorris.Util.MiscUtil;

public class RespawnThread extends DataManipulator implements ZAThread {
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
            MiscUtil.sendPlayerMessage(player, ChatColor.GRAY + "You will respawn at the beginning of the next level.");
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
