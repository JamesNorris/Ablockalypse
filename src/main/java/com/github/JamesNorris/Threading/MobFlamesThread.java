package com.github.JamesNorris.Threading;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Implementation.GameHellHound;
import com.github.JamesNorris.Interface.ZAThread;

public class MobFlamesThread extends DataManipulator implements ZAThread {
    private boolean runThrough = false;
    private int count = 0, interval;

    public MobFlamesThread(boolean autorun, int interval) {
        if (autorun)
            setRunThrough(true);
        this.interval = interval;
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    @Override public void run() {
        if (data.hellhounds != null)
            for (GameHellHound f : data.hellhounds)
                if (!f.getWolf().isDead())
                    f.addFlames();
    }

    @Override public void remove() {
        data.threads.remove(this);
    }

    @Override public int getCount() {
        return count;
    }

    @Override public int getInterval() {
        return interval;
    }

    @Override public void setCount(int i) {
        count = i;
    }

    @Override public void setInterval(int i) {
        interval = i;
    }
}
