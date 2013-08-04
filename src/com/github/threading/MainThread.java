package com.github.threading;

import org.bukkit.Bukkit;

import com.github.Ablockalypse;
import com.github.behavior.ZADelayedTask;
import com.github.behavior.ZARepeatingTask;
import com.github.behavior.ZAScheduledTask;
import com.github.behavior.ZAThread;

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
            Ablockalypse.crash("The main thread has been run several times over. Ablockalypse will now stop to prevent serious issues.", Integer.MAX_VALUE);
            return;
        }
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.getInstance(), new Runnable() {
            @Override public void run() {
                tick();
            }
        }, interval, interval);
    }

    public synchronized ZAThread scheduleDelayedTask(final ZAScheduledTask task, final int delay) {
        ZADelayedTask thread = new ZADelayedTask() {
            int countup = 0;

            @Override public int getCountup() {
                return countup;
            }

            @Override public int getDelay() {
                return delay;
            }

            @Override public void remove() {
                Ablockalypse.getData().objects.remove(this);
            }

            @Override public void run() {
                task.run();
            }

            @Override public void setCountup(int countup) {
                this.countup = countup;
            }
        };
        Ablockalypse.getData().objects.add(thread);
        return thread;
    }

    public synchronized ZAThread scheduleRepeatingTask(final ZAScheduledTask task, final int interval) {
        ZARepeatingTask thread = new ZARepeatingTask() {
            int count = 0, interval = 1;
            boolean runThrough = false;

            @Override public int getCount() {
                return count;
            }

            @Override public int getInterval() {
                return interval;
            }

            @Override public void remove() {
                Ablockalypse.getData().objects.remove(this);
            }

            @Override public void run() {
                task.run();
            }

            @Override public boolean runThrough() {
                return runThrough;
            }

            @Override public void setCount(int i) {
                count = i;
            }

            @Override public void setInterval(int i) {
                interval = i;
            }

            @Override public void setRunThrough(boolean tf) {
                runThrough = tf;
            }
        };
        thread.setInterval(interval);
        thread.setRunThrough(true);
        Ablockalypse.getData().objects.add(thread);
        return thread;
    }

    public synchronized void tick() {
        for (ZAThread thread : Ablockalypse.getData().getObjectsOfType(ZAThread.class)) {
            if (thread instanceof ZARepeatingTask) {
                ZARepeatingTask rthread = (ZARepeatingTask) thread;
                rthread.setCount(rthread.getCount() + 1);
                if (rthread.runThrough() && rthread.getCount() >= rthread.getInterval()) {
                    rthread.run();
                    rthread.setCount(0);
                }
            } else if (thread instanceof ZADelayedTask) {
                ZADelayedTask dthread = (ZADelayedTask) thread;
                dthread.setCountup(dthread.getCountup() + 1);
                if (dthread.getCountup() >= dthread.getDelay()) {
                    dthread.run();
                    dthread.remove();
                }
            }
        }
    }
}
