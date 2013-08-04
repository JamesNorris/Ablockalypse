package com.github.threading.inherent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.ZAPlayer;
import com.github.behavior.ZARepeatingTask;
import com.github.enumerated.Setting;

public class LastStandPickupThread implements ZARepeatingTask {
    private DataContainer data = Ablockalypse.getData();
    private int interval, count = 0, starting, max, perRequirement, ranThrough = 0, requirement;
    private ZAPlayer pickup, downed;
    private Player pickupPlayer, downedPlayer;
    private boolean runThrough;

    public LastStandPickupThread(ZAPlayer pickup, ZAPlayer downed, int interval, int requirement, boolean autorun) {
        this.pickup = pickup;
        this.downed = downed;
        this.interval = interval;
        this.requirement = requirement;
        pickupPlayer = pickup.getPlayer();
        downedPlayer = downed.getPlayer();
        pickupPlayer.sendMessage(ChatColor.GRAY + "Stay near the player to pick them up!");
        starting = (int) pickupPlayer.getExp();
        max = pickupPlayer.getExpToLevel() - 1;
        perRequirement = max / requirement;
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
        pickupPlayer.setExp(starting);
        data.objects.remove(this);
    }

    @Override public void run() {
        ++ranThrough;
        if (downed.isInLastStand() && pickupPlayer.getLocation().distanceSquared(downedPlayer.getLocation()) <= 4 /* 4 = 2 squared */&& ranThrough < requirement) {
            pickupPlayer.setExp(starting + perRequirement * ranThrough);
        } else if (ranThrough >= requirement && downed.isInLastStand()) {
            downed.toggleLastStand();
            pickupPlayer.sendMessage(ChatColor.GREEN + "You have picked up " + downedPlayer.getName() + "!");
            pickup.addPoints((Integer) Setting.LAST_STAND_HELPER_PAY.getSetting());
            remove();
        } else {
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
        data.objects.add(this);
    }
}
