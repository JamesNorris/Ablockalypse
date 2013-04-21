package com.github.jamesnorris.threading;

import org.bukkit.ChatColor;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.event.bukkit.PlayerInteract;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZADelayedThread;

public class TeleporterLinkageTimerThread implements ZADelayedThread {
    private DataContainer data = DataContainer.data;
    private int delay, countup = 0;
    private Mainframe frame;
    private ZAPlayer zap;
    private boolean canlink = true, linked = false;

    public TeleporterLinkageTimerThread(Mainframe frame, ZAPlayer zap, int delay) {
        this.delay = delay;
        this.frame = frame;
        this.zap = zap;
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }

    public void setLinked(boolean tf) {
        linked = tf;
    }

    @Override public void run() {
        canlink = false;
        if (PlayerInteract.mainframeLinkers.containsKey(zap)) {
            if (!linked) {
                zap.getPlayer().sendMessage(ChatColor.RED + "You were too late to link the teleporter.");
            }
            PlayerInteract.mainframeLinkers.remove(zap);
            PlayerInteract.mainframeLinkers_Timers.remove(zap);
        }
    }

    public Mainframe getMainframe() {
        return frame;
    }

    public ZAPlayer getZAPlayer() {
        return zap;
    }

    public boolean canBeLinked() {
        return canlink;
    }

    @Override public void remove() {
        data.threads.remove(this);
    }

    @Override public int getDelay() {
        return delay;
    }

    @Override public int getCountup() {
        return countup;
    }

    @Override public void setCountup(int countup) {
        this.countup = countup;
    }
}
