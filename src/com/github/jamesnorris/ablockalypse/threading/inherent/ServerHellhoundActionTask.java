package com.github.jamesnorris.ablockalypse.threading.inherent;

import org.bukkit.Location;
import org.bukkit.entity.Wolf;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.entity.Hellhound;
import com.github.jamesnorris.ablockalypse.enumerated.ZAEffect;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;
import com.github.jamesnorris.ablockalypse.utility.BukkitUtility;

public class ServerHellhoundActionTask extends RepeatingTask {
    private static final int RE_TELEPORT_WAIT = 400;// ticks
    private int countup_to_teleport = 0;
    private DataContainer data = Ablockalypse.getData();

    public ServerHellhoundActionTask(int interval, boolean autorun) {
        super(interval, autorun);
    }

    @Override public void run() {
        for (Hellhound f : data.getObjectsOfType(Hellhound.class)) {
            Wolf wolf = f.getWolf();
            if (wolf.isDead()) {
                continue;
            }
            ZAEffect.FLAMES.play(wolf.getLocation());
            f.setAggressive(true);
            if (++countup_to_teleport >= RE_TELEPORT_WAIT / getInterval()) {
                countup_to_teleport = 0;
                Location target = f.getGame().getRandomLivingPlayer().getLocation();
                Location strike = BukkitUtility.getNearbyLocation(target, 2, 4, 0, 0, 2, 4);
                wolf.teleport(strike);
                strike.getWorld().strikeLightning(strike);
            }
        }
    }
}
