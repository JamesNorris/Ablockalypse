package com.github.threading;

public class RepeatingTask extends Task {
    private int interval, count;

    public RepeatingTask(int interval) {
        this(interval, false);
    }

    public RepeatingTask(int interval, boolean autorun) {
        super(autorun);
        this.interval = interval;
    }

    public int getCount() {
        return count;
    }

    public int getInterval() {
        return interval;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
