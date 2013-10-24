package com.github.queue;

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
    
    public void run() {
        //nothing
    }
    
    public boolean removeAfterRun() {
        return true;
    }
}
