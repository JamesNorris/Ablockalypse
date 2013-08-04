package com.github.behavior;

public interface ZADelayedTask extends ZAThread {
    public int getCountup();

    public int getDelay();

    public void setCountup(int countup);
}
