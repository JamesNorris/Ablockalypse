package com.github.jamesnorris.inter;

public interface ZARepeatingThread extends ZAThread {
    public boolean runThrough();

    public void setRunThrough(boolean tf);

    public int getCount();

    public int getInterval();

    public void setCount(int i);

    public void setInterval(int i);
}
