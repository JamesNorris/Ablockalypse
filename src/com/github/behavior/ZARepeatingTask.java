package com.github.behavior;

public interface ZARepeatingTask extends ZAThread {
    public int getCount();

    public int getInterval();

    public boolean runThrough();

    public void setCount(int i);

    public void setInterval(int i);

    public void setRunThrough(boolean tf);
}
