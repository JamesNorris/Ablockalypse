package com.github.jamesnorris.ablockalypse.queue;

import java.util.HashMap;
import java.util.Map;

public class QueuedData {
    protected final String key;
    public Map<String, Object> details = new HashMap<String, Object>();

    public QueuedData(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public boolean removeAfterRun() {
        return true;
    }

    public void run() {
        // nothing
    }
}
