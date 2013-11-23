package com.github.threading.inherent;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.entity.ZAPlayer;
import com.github.enumerated.Setting;
import com.github.threading.RepeatingTask;

public class LastStandPickupTask extends RepeatingTask {
    private DataContainer data = Ablockalypse.getData();
    private static List<String> pickingUp = new ArrayList<String>();
    private int starting, max, perRequirement, ranThrough = 0, requirement;
    private ZAPlayer pickup, downed;
    private Player pickupPlayer, downedPlayer;

    public LastStandPickupTask(ZAPlayer pickup, ZAPlayer downed, int interval, int requirement, boolean autorun) {
        super(interval, autorun);
        if (pickingUp.contains(downed.getPlayer().getName())) {
            return;
        }
        pickingUp.add(downed.getPlayer().getName());
        this.pickup = pickup;
        this.downed = downed;
        this.requirement = requirement;
        pickupPlayer = pickup.getPlayer();
        downedPlayer = downed.getPlayer();
        starting = (int) pickupPlayer.getExp();
        max = pickupPlayer.getExpToLevel() - 1;
        perRequirement = max / requirement;
    }

    @Override public void cancel() {
        pickingUp.remove(downed.getPlayer().getName());
        pickupPlayer.setExp(starting);
        data.objects.remove(this);
    }

    @Override public void run() {
        ++ranThrough;
        if (downed.isInLastStand() && pickupPlayer.getLocation().distanceSquared(downedPlayer.getLocation()) <= 4 /* 4 = 2 squared */&& ranThrough < requirement
                && pickupPlayer.isSneaking()) {
            pickupPlayer.setExp(starting + perRequirement * ranThrough);
        } else if (ranThrough >= requirement && downed.isInLastStand()) {
            downed.toggleLastStand();
            pickupPlayer.sendMessage(ChatColor.GREEN + "You have picked up " + downedPlayer.getName() + "!");
            pickup.addPoints((Integer) Setting.LAST_STAND_HELPER_PAY.getSetting());
            cancel();
        } else {
            cancel();
        }
    }
}
