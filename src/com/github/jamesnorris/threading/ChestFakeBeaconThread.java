package com.github.jamesnorris.threading;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.event.bukkit.EntityExplode;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.MysteryChest;
import com.github.jamesnorris.inter.ZARepeatingThread;
import com.github.jamesnorris.util.MiscUtil;

public class ChestFakeBeaconThread extends DataManipulator implements ZARepeatingThread {
    private int count = 0, interval;
    private boolean runThrough = false;
    private Game game;
    private MysteryChest active;
    private ArrayList<Location> fireLocations = new ArrayList<Location>();

    public ChestFakeBeaconThread(Game game, int interval, boolean autorun) {
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
        if ((Boolean) Setting.BEACONS.getSetting()) {
            if (game == null || game.getFakeBeaconThread() != this) {
                remove();
            } else {
                if (game.getActiveMysteryChest() == null) {
                    game.setActiveMysteryChest(game.getRandomMysteryChest());
                }
                if (game.hasStarted() && game.getActiveMysteryChest() != null) {
                    if (active == null || !MiscUtil.locationMatch(game.getActiveMysteryChest().getLocation(), active.getLocation())) {
                        active = game.getActiveMysteryChest();
                        fireLocations = getFiringLocations(active.getLocation());
                    }
                    for (Location l : fireLocations) {
                        Builder effect = FireworkEffect.builder().trail(true).flicker(false).withColor(Color.BLUE).with(Type.BURST);
                        Firework work = (Firework) l.getWorld().spawn(l, Firework.class);
                        EntityExplode.preventExplosion(work.getUniqueId(), true);
                        FireworkMeta meta = work.getFireworkMeta();
                        meta.addEffect(effect.build());
                        meta.setPower(5);
                        work.setFireworkMeta(meta);
                    }
                }
            }
        }
    }

    private ArrayList<Location> getFiringLocations(Location loc) {
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(loc.clone().add(0, 1, 0));
        // World world = loc.getWorld();
        // for (int y = loc.getBlockY(); y < world.getHighestBlockAt(loc).getLocation().clone().add(0, 1, 0).getBlockY(); y++) {
        // Location newloc = world.getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).getLocation();
        // if (newloc.getBlock().isEmpty() && !newloc.clone().subtract(0, 1, 0).getBlock().isEmpty()) {
        // locations.add(newloc);
        // }
        // }
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
