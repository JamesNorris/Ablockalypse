package com.github.threading;

import org.bukkit.Bukkit;

import com.github.Ablockalypse;

public class MainThread {
    private int id = -1, interval = 1;

    public MainThread(boolean autorun) {
        if (autorun) {
            run();
        }
    }

    public synchronized void cancel() {
        Bukkit.getScheduler().cancelTask(id);
        id = -1;
    }

    public void run() {
        if (id != -1) {
            Ablockalypse.getErrorTracker().crash("The main thread has been run several times over. Ablockalypse will now stop to prevent serious issues.", Integer.MAX_VALUE);
            return;
        }
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.getInstance(), new Runnable() {
            @Override public void run() {
                tick();
            }
        }, interval, interval);
    }

    public synchronized void tick() {
        for (Task task : Ablockalypse.getData().getObjectsOfType(Task.class)) {
            if (task.isRunning()) {
                if (task instanceof RepeatingTask) {
                    RepeatingTask rthread = (RepeatingTask) task;
                    rthread.setCount(rthread.getCount() + 1);
                    if (rthread.getCount() >= rthread.getInterval()) {
                        rthread.run();
                        rthread.setCount(0);
                    }
                } else if (task instanceof DelayedTask) {
                    DelayedTask dthread = (DelayedTask) task;
                    dthread.setCountup(dthread.getCountup() + 1);
                    if (dthread.getCountup() >= dthread.getDelay()) {
                        dthread.run();
                        dthread.cancel();
                    }
                }
            }
        }
    }
}
