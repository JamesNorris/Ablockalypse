package com.github.threading.inherent;

import org.bukkit.ChatColor;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Mainframe;
import com.github.aspect.ZAPlayer;
import com.github.behavior.ZADelayedTask;
import com.github.event.bukkit.PlayerInteract;

public class TeleporterLinkageTimerThread implements ZADelayedTask {
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
        data.objects.remove(this);
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
        data.objects.add(this);
    }
}
