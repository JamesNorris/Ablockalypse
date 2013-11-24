package com.github.jamesnorris.ablockalypse.threading.inherent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.block.Claymore;
import com.github.jamesnorris.ablockalypse.aspect.entity.ZAMob;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;

public class ClaymoreActionTask extends RepeatingTask {
    private static final int INTERVAL = 20;
    private Claymore claymore;
    private DataContainer data = Ablockalypse.getData();

    public ClaymoreActionTask(Claymore claymore, boolean autorun) {
        super(INTERVAL, autorun);
        this.claymore = claymore;
    }

    @Override public void run() {
        if (claymore == null || !claymore.getGame().hasStarted()) {
            cancel();
            return;
        }
        for (ZAMob mob : data.getObjectsOfType(ZAMob.class)) {
            if (mob.getGame().getUUID().compareTo(claymore.getGame().getUUID()) == 0 && claymore.isWithinExplosionDistance(mob.getEntity().getLocation())) {
                claymore.trigger();
                cancel();
                return;
            }
        }
    }
}
