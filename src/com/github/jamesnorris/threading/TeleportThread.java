package com.github.jamesnorris.threading;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZARepeatingThread;
import com.github.jamesnorris.util.MiscUtil;

public class TeleportThread implements ZARepeatingThread {
    private DataContainer data = Ablockalypse.getData();
    private Location loc;
    private Player player;
    private boolean runThrough = false;
    private int time, count = 0, interval;
    private ZAPlayer zaplayer;

    /**
     * Creates an instance of the thread for teleporting a player.
     * 
     * @param zaplayer The player to countdown for, as a ZAPlayer instance
     * @param time The time before the countdown stops
     * @param autorun Whether or not to run the thread automatically
     * @param interval The time in ticks between phase changes of the blinker
     */
    public TeleportThread(ZAPlayer zaplayer, int time, boolean autorun, int interval) {
        this.zaplayer = zaplayer;
        this.time = time;
        this.interval = interval;
        player = zaplayer.getPlayer();
        loc = zaplayer.getPlayer().getLocation();
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
        zaplayer.setTeleporting(true);
        if (time != 0) {
            if (!sameLocation()) {
                remove();
                player.sendMessage(ChatColor.GRAY + "Teleportation cancelled!");
                zaplayer.setTeleporting(false);
                return;
            }
            player.sendMessage(ChatColor.GRAY + "" + time + " seconds to teleport...");
            ZAEffect.TELEPORTATION.play(player.getLocation());
            --time;
        } else if (time <= 0) {
            ZAEffect.SMOKE.play(player.getLocation());
            zaplayer.sendToMainframe("Teleport");
            ZAEffect.SMOKE.play(player.getLocation());
            zaplayer.setTeleporting(false);
            remove();
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

    /* Checks if the player is in roughly the same location as they were when they started the thread. */
    private boolean sameLocation() {
        return MiscUtil.locationMatch(player.getLocation(), loc);
    }
}
