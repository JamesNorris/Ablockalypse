package com.github.jamesnorris.ablockalypse.threading.inherent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import com.github.jamesnorris.ablockalypse.aspect.block.MysteryBox;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.event.bukkit.EntityExplode;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;
import com.github.jamesnorris.ablockalypse.utility.BukkitUtility;

public class MysteryBoxFakeBeaconTask extends RepeatingTask {
    private ArrayList<Location> fireLocations = new ArrayList<Location>();
    private Random rand = new Random();
    private MysteryBox active;
    private Game game;

    public MysteryBoxFakeBeaconTask(Game game, int interval, boolean autorun) {
        super(interval, autorun);
        this.game = game;
    }

    @Override public void run() {
        if ((Boolean) Setting.BEACONS.getSetting()) {
            if (game == null || game.getFakeBeaconThread() != this) {
                cancel();
                return;
            }
            if (game.getActiveMysteryChest() == null) {
                List<MysteryBox> chests = game.getObjectsOfType(MysteryBox.class);
                if (chests.size() > 0) {
                    game.setActiveMysteryChest(chests.get(rand.nextInt(chests.size())));
                }
            }
            if (game.hasStarted() && game.getActiveMysteryChest() != null) {
                if (active == null || !BukkitUtility.locationMatch(game.getActiveMysteryChest().getLocation(), active.getLocation())) {
                    active = game.getActiveMysteryChest();
                    fireLocations = getFiringLocations(active.getLocation());
                }
                for (Location l : fireLocations) {
                    Builder effect = FireworkEffect.builder().trail(true).flicker(false).withColor(Color.BLUE).with(Type.BURST);
                    Firework work = l.getWorld().spawn(l, Firework.class);
                    EntityExplode.preventExplosion(work.getUniqueId(), true);
                    FireworkMeta meta = work.getFireworkMeta();
                    meta.addEffect(effect.build());
                    meta.setPower(5);
                    work.setFireworkMeta(meta);
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
}
