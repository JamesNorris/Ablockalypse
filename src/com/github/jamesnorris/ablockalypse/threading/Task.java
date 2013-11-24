package com.github.jamesnorris.ablockalypse.threading;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;

public class Task {
    private DataContainer data = Ablockalypse.getData();
    private boolean runThrough;

    public Task(boolean autorun) {
        runThrough = autorun;
        data.objects.add(this);
    }

    public void cancel() {
        data.objects.remove(this);
    }

    public boolean isRunning() {
        return runThrough;
    }

    public void run() {}

    public void setRunning(boolean runThrough) {
        this.runThrough = runThrough;
    }
}
