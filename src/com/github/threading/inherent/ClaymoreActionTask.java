package com.github.threading.inherent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.block.Claymore;
import com.github.aspect.entity.ZAMob;
import com.github.threading.RepeatingTask;

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
