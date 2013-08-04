package com.github.behavior;

public interface ZAThread extends ZAScheduledTask {
    public void remove();

    @Override public void run();
}
