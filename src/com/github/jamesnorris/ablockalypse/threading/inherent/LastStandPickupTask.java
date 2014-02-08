package com.github.jamesnorris.ablockalypse.threading.inherent;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.ZAPlayer;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;

public class LastStandPickupTask extends RepeatingTask {
    private DataContainer data = Ablockalypse.getData();
    private static List<String> pickingUp = new ArrayList<String>();
    private int starting, max, perRequirement, ranThrough = 0, requirement;
    private ZAPlayer pickup, downed;
    private Player pickupPlayer, downedPlayer;

    public LastStandPickupTask(ZAPlayer pickup, ZAPlayer downed, int interval, int requirement, boolean autorun) {
        super(interval, autorun);
        if (pickup.equals(downed) || pickingUp.contains(downed.getPlayer().getName())) {
            super.cancel();
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
        if (downed.isInLastStand() && pickupPlayer.getLocation().distanceSquared(downedPlayer.getLocation()) <= 4 /* 4 = 2 squared */
                && ranThrough < requirement && pickupPlayer.isSneaking()) {
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
