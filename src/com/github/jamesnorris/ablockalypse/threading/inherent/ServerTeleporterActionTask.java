package com.github.jamesnorris.ablockalypse.threading.inherent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.Teleporter;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;

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
