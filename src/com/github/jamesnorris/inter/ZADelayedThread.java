package com.github.jamesnorris.inter;

public interface ZADelayedThread extends ZAThread {
    public int getDelay();

    public int getCountup();

    public void setCountup(int countup);
}
