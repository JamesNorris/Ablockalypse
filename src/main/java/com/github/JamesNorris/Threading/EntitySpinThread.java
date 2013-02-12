package com.github.JamesNorris.Threading;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Interface.ZAThread;

public class EntitySpinThread extends DataManipulator implements ZAThread {
    private int interval, count = 0, revolutions, degrees;
    private Entity entity;
    private boolean runThrough = false;
    private float current = 0F;

    public EntitySpinThread(Entity entity, int revolutions, int degrees, boolean autorun, int interval) {
        this.entity = entity;
        this.revolutions = revolutions;
        this.degrees = degrees;
        this.interval = interval;
        if (autorun)
            setRunThrough(true);
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }
    
    public int getDegrees() {
        return degrees;
    }
    
    public void setDegrees(int degrees) {
        this.degrees = degrees;
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    @Override public void run() {
        if (revolutions > 0) {
            Location loc = entity.getLocation();
            current += (float) degrees;
            revolutions -= ((int) current) / 360;
            loc.setYaw(loc.getYaw() + (float) degrees);
            entity.teleport(loc);
        } else {
            remove();
        }
    }

    @Override public void remove() {
        data.threads.remove(this);
    }

    @Override public int getCount() {
        return count;
    }

    @Override public int getInterval() {
        return interval;
    }

    @Override public void setCount(int i) {
        count = i;
    }

    @Override public void setInterval(int i) {
        interval = i;
    }
}
