package com.github.JamesNorris.Threading;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.ZAEffect;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Interface.ZAThread;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.MiscUtil;

public class TeleportThread extends DataManipulator implements ZAThread {
    private Location loc;
    private Player player;
    private int time, count = 0, interval;
    private ZAPlayer zaplayer;
    private boolean runThrough = false;

    /**
     * Creates an instance of the thread for teleporting a player.
     * 
     * @param zaplayer The player to countdown for, as a ZAPlayer instance
     * @param time The time before the countdown stops
     * @param countdown Whether or not to run the thread automatically
     */
    public TeleportThread(ZAPlayer zaplayer, int time, boolean autorun, int interval) {
        this.zaplayer = zaplayer;
        this.time = time;
        this.interval = interval;
        player = zaplayer.getPlayer();
        loc = zaplayer.getPlayer().getLocation();
        if (autorun)
            setRunThrough(true);
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }

    private void sendPlayerMessage(Player player, String message) {
        MiscUtil.sendPlayerMessage(player, message);
    }

    /*
     * Checks if the player is in roughly the same location as they were when they started the thread.
     */
    private boolean sameLocation() {
        if (player.getLocation().getBlockX() == loc.getBlockX() && player.getLocation().getBlockY() == loc.getBlockY() && player.getLocation().getBlockZ() == loc.getBlockZ())
            return true;
        return false;
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
        zaplayer.setTeleporting(true);
        if (time != 0) {
            if (!sameLocation()) {
                remove();
                sendPlayerMessage(player, ChatColor.GRAY + "Teleportation cancelled!");
                zaplayer.setTeleporting(false);
                return;
            } else {
                sendPlayerMessage(player, ChatColor.GRAY + "" + time + " seconds to teleport...");
                --time;
            }
        } else if (time <= 0) {
            EffectUtil.generateEffect(player, player.getLocation(), ZAEffect.SMOKE);
            zaplayer.sendToMainframe("Teleport");
            EffectUtil.generateEffect(player, player.getLocation(), ZAEffect.SMOKE);
            zaplayer.setTeleporting(false);
            remove();
        }
    }

    @Override public void remove() {
        data.threads.remove(this);
    }
}
