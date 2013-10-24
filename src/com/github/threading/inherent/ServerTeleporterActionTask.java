package com.github.threading.inherent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.block.Teleporter;
import com.github.threading.RepeatingTask;

public class ServerTeleporterActionTask extends RepeatingTask {
    private DataContainer data = Ablockalypse.getData();

    public ServerTeleporterActionTask(int interval, boolean autorun) {
        super(interval, autorun);
    }

    @Override public void run() {
        for (Teleporter tele : data.getObjectsOfType(Teleporter.class)) {
            if (tele.isLinked()) {
                tele.playEffects(Teleporter.LINKED_EFFECTS);
            }
        }
    }
}
