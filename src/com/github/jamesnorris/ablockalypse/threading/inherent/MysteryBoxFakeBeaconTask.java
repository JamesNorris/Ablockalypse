package com.github.jamesnorris.ablockalypse.threading.inherent;

import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;

import com.github.jamesnorris.ablockalypse.aspect.Game;
import com.github.jamesnorris.ablockalypse.aspect.MysteryBox;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;
import com.github.jamesnorris.ablockalypse.utility.BukkitUtility;

public class MysteryBoxFakeBeaconTask extends RepeatingTask {
    // private Location fireLocation;
    // private FireworkEffect effect = FireworkEffect.builder().trail(true).flicker(false).withColor(Color.BLUE).with(Type.BURST).build();
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
                    // fireLocation = getFiringLocation(active.getLocation());
                }
                for (int y = 255; y > active.getLocation().getBlockY(); y--) {
                    Location newLoc = active.getLocation().clone();
                    newLoc.setY(y);
                    newLoc.getWorld().playEffect(newLoc, Effect.MOBSPAWNER_FLAMES, 1);
                }
                // Firework work = fireLocation.getWorld().spawn(fireLocation, Firework.class);
                // EntityExplode.preventExplosion(work.getUniqueId(), true);
                // FireworkMeta meta = work.getFireworkMeta();
                // meta.addEffect(effect);
                // meta.setPower(5);
                // work.setFireworkMeta(meta);
            }
        }
    }
    // private Location getFiringLocation(Location loc) {
    // // return loc.clone().add(0, 1, 0);
    // for (int y = 255; y > loc.getBlockY(); y--) {
    // Location newloc = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).getLocation();
    // if (newloc.getBlock().isEmpty() && !newloc.clone().subtract(0, 1, 0).getBlock().isEmpty()) {
    // return newloc;
    // }
    // }
    // return loc;
    // }
}
