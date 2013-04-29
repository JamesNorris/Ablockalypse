package com.github.jamesnorris.threading;

import org.bukkit.ChatColor;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.event.bukkit.PlayerInteract;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZADelayedThread;

public class TeleporterLinkageTimerThread implements ZADelayedThread {
    private boolean canlink = true, linked = false;
    private DataContainer data = Ablockalypse.getData();
    private int delay, countup = 0;
    private Mainframe frame;
    private ZAPlayer zap;

    public TeleporterLinkageTimerThread(Mainframe frame, ZAPlayer zap, int delay) {
        this.delay = delay;
        this.frame = frame;
        this.zap = zap;
        addToThreads();
    }

    public boolean canBeLinked() {
        return canlink;
    }

    @Override public int getCountup() {
        return countup;
    }

    @Override public int getDelay() {
        return delay;
    }

    public Mainframe getMainframe() {
        return frame;
    }

    public ZAPlayer getZAPlayer() {
        return zap;
    }

    @Override public void remove() {
        data.threads.remove(this);
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

    @Override public void setCountup(int countup) {
        this.countup = countup;
    }

    public void setLinked(boolean tf) {
        linked = tf;
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }
}
