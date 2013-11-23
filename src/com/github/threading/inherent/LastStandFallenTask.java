package com.github.threading.inherent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.entity.ZAPlayer;
import com.github.threading.RepeatingTask;
import com.github.threading.Task;
import com.github.utility.AblockalypseUtility;

public class LastStandFallenTask extends RepeatingTask {
    private static final int INTERVAL = 20;
    private DataContainer data = Ablockalypse.getData();
    private Player player;
    private ZAPlayer zap;
    private Task warning;

    /**
     * Creates a new LastStandThread instance.
     * 
     * @param zap The ZAPlayer to hurt
     * @param interval The interval between damage to the player
     * @param autorun Whether or not the thread will automatically run
     */
    public LastStandFallenTask(ZAPlayer zap, boolean autorun) {
        super(INTERVAL, autorun);
        this.zap = zap;
        player = zap.getPlayer();
    }

    @Override public void run() {
        if (!zap.isInLastStand() || player.isDead()) {
            cancel();
            return;
        }
        if (warning != null) {
            data.objects.remove(warning);
        }
        warning = AblockalypseUtility.scheduleNearbyWarning(player.getLocation(), ChatColor.GRAY + "Hold " + ChatColor.AQUA + "SHIFT" + ChatColor.GRAY + " to pick up "
                + player.getName() + ".", 2, 3.5, 2, 10000);
        player.damage(.25);
    }
}
