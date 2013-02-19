package com.github.JamesNorris.Threading;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Interface.ZAThread;

public class FakeBeaconThread extends DataManipulator implements ZAThread {
    private int count = 0, interval;
    private boolean runThrough = false;
    private ZAGameBase game;

    public FakeBeaconThread(ZAGameBase game, int interval, boolean autorun) {
        this.game = game;
        this.interval = interval;
        if (autorun)
            setRunThrough(true);
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    @Override public void run() {
        if (game == null)
            remove();
        if (game.hasStarted()) {
            for (Location l : getAllSpacesAbove(game.getActiveMysteryChest().getLocation())) {
                Builder effect = FireworkEffect.builder().trail(true).flicker(false).withColor(Color.BLUE).with(Type.BURST);
                Firework work = (Firework) l.getWorld().spawn(l, Firework.class);
                FireworkMeta meta = work.getFireworkMeta();
                meta.addEffect(effect.build());
                meta.setPower(99999);
                work.setFireworkMeta(meta);
            }
        }
    }

    private ArrayList<Location> getAllSpacesAbove(Location loc) {
        ArrayList<Location> locations = new ArrayList<Location>();
        for (int y = loc.getBlockY(); y < 200; y++) {
            Location newloc = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).getLocation();
            if (newloc.getBlock().isEmpty() && !newloc.clone().subtract(0, 1, 0).getBlock().isEmpty()) {
                locations.add(newloc);
            }
        }
        return locations;
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
