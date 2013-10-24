package com.github.threading;

import com.github.Ablockalypse;
import com.github.DataContainer;

public class Task {
    private DataContainer data = Ablockalypse.getData();
    private boolean runThrough;
    
    public Task(boolean autorun) {
        runThrough = autorun;
        data.objects.add(this);
    }
    
    public void run() {}
    
    public void cancel() {
        data.objects.remove(this);
    }
    
    public boolean isRunning() {
        return runThrough;
    }
    
    public void setRunning(boolean runThrough) {
        this.runThrough = runThrough;
    }
}
