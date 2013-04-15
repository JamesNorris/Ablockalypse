package com.github.jamesnorris.threading;

import org.bukkit.Bukkit;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.inter.ZADelayedThread;
import com.github.jamesnorris.inter.ZARepeatingThread;
import com.github.jamesnorris.inter.ZAThread;

public class MainThread extends DataManipulator {
    private int id = -1, interval = 1;

    public MainThread(boolean autorun) {
        if (autorun)
            run();
    }

    public void run() {
        if (id == -1) {
            id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
                @Override public void run() {
                    tick();
                }
            }, interval, interval);
        } else {
            Ablockalypse.crash("The main thread has been run several times over. Ablockalypse will now stop to prevent serious issues.", true);
        }
    }

    public synchronized void tick() {
        for (ZAThread thread : data.threads) {
            if (thread instanceof ZARepeatingThread) {
                ZARepeatingThread rthread = (ZARepeatingThread) thread;
                rthread.setCount(rthread.getCount() + 1);
                if (rthread.runThrough() && (rthread.getCount() >= rthread.getInterval())) {
                    rthread.run();
                    rthread.setCount(0);
                }
            } else if (thread instanceof ZADelayedThread) {
                ZADelayedThread dthread = (ZADelayedThread) thread;
                dthread.setCountup(dthread.getCountup() + 1);
                if (dthread.getCountup() >= dthread.getDelay()) {
                    dthread.run();
                    dthread.remove();
                }
            }
        }
    }

    public synchronized void cancel() {
        Bukkit.getScheduler().cancelTask(id);
        id = -1;
    }
}
