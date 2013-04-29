package com.github.jamesnorris.inter;

public interface ZADelayedThread extends ZAThread {
    public int getCountup();

    public int getDelay();

    public void setCountup(int countup);
}
