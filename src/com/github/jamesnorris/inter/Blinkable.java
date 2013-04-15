package com.github.jamesnorris.inter;

import com.github.jamesnorris.threading.BlinkerThread;

public interface Blinkable {
    /**
     * Gets the BlinkerThread attached to this instance.
     * 
     * @return The BlinkerThread attached to this instance
     */
    public BlinkerThread getBlinkerThread();

    /**
     * Stops/Starts the blinker for this instance.
     * 
     * @param tf Whether or not this instance should blink
     */
    public void setBlinking(boolean tf);
}
