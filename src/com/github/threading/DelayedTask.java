package com.github.threading;

public class DelayedTask extends Task {
    private int countUp, delay;

    public DelayedTask(int delay, boolean autorun) {
        super(autorun);
        this.delay = delay;
    }

    public int getCountup() {
        return countUp;
    }

    public int getDelay() {
        return delay;
    }

    public void setCountup(int countUp) {
        this.countUp = countUp;
    }
}
