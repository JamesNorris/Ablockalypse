package com.github.behavior;

import com.github.threading.inherent.BlinkerTask;

public interface Blinkable extends GameAspect {
    /**
     * Gets the {@link BlinkerTask.java} object associated with this aspect,
     * that runs on the Ablockalypse main thread, and allows the aspect to blink.
     * 
     * @return The task that allows the aspect to blink
     */
    public BlinkerTask getBlinkerTask();

    /**
     * Returns whether or not the aspect is blinking. This is functionally the same as the method {@code getBlinkerTask().isRunning()}.
     * 
     * @return Whether or not the aspect is blinking
     */
    public boolean isBlinking();

    /**
     * Sets whether or not the aspect should blink. This is functionally the same as the method {@code getBlinkerTask().setRunning(true/false)}.
     * The aspect will then start or stop blinking as told to do so through this method, as soon as this method is called, or shortly thereafter.
     * 
     * @param blinking Whether or not the aspect should start blinking
     */
    public void setBlinking(boolean blinking);
}
