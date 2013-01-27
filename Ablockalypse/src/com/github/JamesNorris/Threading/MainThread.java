package com.github.JamesNorris.Threading;

import org.bukkit.Bukkit;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Interface.ZAThread;

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
        for (ZAThread thread : data.thread) {
            thread.setCount(thread.getCount() + 1);
            if (thread.runThrough() && (thread.getCount() >= thread.getInterval())) {
                thread.run();
                thread.setCount(0);
            }
        }
    }

    public synchronized void cancel() {
        Bukkit.getScheduler().cancelTask(id);
        id = -1;
    }
}
