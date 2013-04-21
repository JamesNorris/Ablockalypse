package com.github.jamesnorris.threading;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZARepeatingThread;

public class LastStandPickupThread implements ZARepeatingThread {
    private DataContainer data = DataContainer.data;
    private boolean runThrough;
    private int interval, count = 0, starting, max, perRequirement, ranThrough = 0, requirement;
    private ZAPlayer pickup, downed;
    private Player pickupPlayer, downedPlayer;

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
        if (autorun)
            setRunThrough(true);
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setRunThrough(boolean tf) {
        this.runThrough = tf;
    }

    @Override public void run() {
        ++ranThrough;
        if (downed.isInLastStand() && pickupPlayer.getLocation().distance(downedPlayer.getLocation()) <= 2 && ranThrough < requirement) {
            pickupPlayer.setExp(starting + (perRequirement * ranThrough));
        } else if (ranThrough >= requirement && downed.isInLastStand()) {
            downed.toggleLastStand();
            pickupPlayer.sendMessage(ChatColor.GREEN + "You have picked up " + downedPlayer.getName() + "!");
            pickup.addPoints((Integer) Setting.LAST_STAND_HELPER_PAY.getSetting());
            remove();
        } else {
            remove();
        }
    }

    @Override public void remove() {
        pickupPlayer.setExp(starting);
        data.threads.remove(this);
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
}
