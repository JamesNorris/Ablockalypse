package com.github.threading.inherent;

import org.bukkit.ChatColor;

import com.github.aspect.block.Teleporter;
import com.github.aspect.entity.ZAPlayer;
import com.github.event.bukkit.PlayerInteract;
import com.github.threading.DelayedTask;

public class TeleporterLinkageTimerTask extends DelayedTask {
    private boolean canlink = true, linked = false;
    private Teleporter frame;
    private ZAPlayer zap;

    public TeleporterLinkageTimerTask(Teleporter frame, ZAPlayer zap, int delay, boolean autorun) {
        super(delay, autorun);
        this.frame = frame;
        this.zap = zap;
    }

    public boolean canBeLinked() {
        return canlink;
    }

    public Teleporter getMainframe() {
        return frame;
    }

    public ZAPlayer getZAPlayer() {
        return zap;
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

    public void setLinked(boolean tf) {
        linked = tf;
    }
}
