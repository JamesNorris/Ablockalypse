package com.github.jamesnorris.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.event.bukkit.PlayerJoin;
import com.github.jamesnorris.implementation.Passage;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.Claymore;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.implementation.MobSpawner;
import com.github.jamesnorris.implementation.MysteryChest;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.util.SerializableLocation;

public class PerGameDataStorage implements Serializable {// TODO annotations
    private static final long serialVersionUID = 7825383085566172198L;
    private final SerializableLocation activechest, mainframe;
    private final HashMap<SerializableLocation, SerializableLocation> areapoints = new HashMap<SerializableLocation, SerializableLocation>();
    private final ArrayList<SerializableLocation> barriers = new ArrayList<SerializableLocation>();
    private final ArrayList<SerializableLocation> chests = new ArrayList<SerializableLocation>();
    private final HashMap<SerializableLocation, String> claymores = new HashMap<SerializableLocation, String>();
    private final int level;
    private final String name;
    private final ArrayList<SerializableLocation> openedareas = new ArrayList<SerializableLocation>();
    private final ArrayList<PerPlayerDataStorage> playerStorage = new ArrayList<PerPlayerDataStorage>();
    private final ArrayList<SerializableLocation> spawns = new ArrayList<SerializableLocation>();

    public PerGameDataStorage(Game game) {// TODO replace this with a MDS + PPDS (redone) + PGDS (redone) bundle
        name = game.getName();
        if (game.getActiveMysteryChest() != null)
            activechest = new SerializableLocation(game.getActiveMysteryChest().getLocation());
        else
            activechest = null;
        for (MysteryChest mc : game.getObjectsOfType(MysteryChest.class))
            chests.add(new SerializableLocation(mc.getLocation()));
        Location mf = game.getMainframe().getLocation();
        mainframe = (mf != null) ? new SerializableLocation(mf) : null;
        level = game.getLevel();
        for (String s : game.getPlayers()) {
            Player p = Bukkit.getPlayer(s);
            Ablockalypse.getData();
            ZAPlayer zap = DataContainer.data.getZAPlayer(p);
            playerStorage.add(new PerPlayerDataStorage((ZAPlayer) zap));
        }
        for (Barrier gb : game.getObjectsOfType(Barrier.class))
            barriers.add(new SerializableLocation(gb.getCenter()));
        for (Passage ga : game.getObjectsOfType(Passage.class)) {
            Location pt1 = ga.getPoint(1);
            Location pt2 = ga.getPoint(2);
            if (pt1 != null && pt2 != null) {
                SerializableLocation point1 = new SerializableLocation(pt1);
                areapoints.put(point1, new SerializableLocation(pt2));
                if (ga.isOpened()) {
                    openedareas.add(point1);
                }
            }
        }
        for (MobSpawner l : game.getObjectsOfType(MobSpawner.class))
            spawns.add(new SerializableLocation(l.getBukkitLocation()));
        for (Claymore clay : game.getObjectsOfType(Claymore.class)) {
            claymores.put(new SerializableLocation(clay.getDefiningBlock().getLocation()), clay.getPlacer().getName());
        }
    }

    public void load(DataContainer data) {
        try {
            String name = getName();
            Game zag = data.getGame(name, true);
            if (getMainframe(zag) != null) {
                zag.setMainframe(getMainframe(zag));
            }
            for (PerPlayerDataStorage spds : getPlayerData()) {
                Player p = Bukkit.getPlayer(spds.getName());
                if (!data.playerExists(p))
                    new ZAPlayer(p, data.getGame(spds.getGameName(), true));
                if (p.isOnline() && data.playerExists(p)) {
                    ZAPlayer zap = (ZAPlayer) data.getZAPlayer(p);
                    if (zag.getLevel() < spds.getGameLevel()) {
                        zag.setLevel(spds.getGameLevel());
                        spds.loadToPlayer(zap);
                    }
                } else {
                    PlayerJoin.offlinePlayers.put(p.getName(), spds);
                }
            }
            int level = getLevel();
            int setLevel = (zag.getPlayers().size() > 0) ? level : 0;
            zag.setLevel(setLevel);
            for (Location l : getBarrierLocations()) {
                new Barrier(l.getBlock(), (Game) data.getGame(name, true));
            }
            for (Location l : getAreaPoints().keySet()) {
                Location l2 = getAreaPoints().get(l);
                if (l2 != null) {
                    Passage a = new Passage((Game) zag, l, l2);
                    if (isAreaOpen(l)) {
                        a.open();
                    }
                }
            }
            for (Location l : getMysteryChestLocations()) {
                Block b = l.getBlock();
                zag.addObject(new MysteryChest(b.getState(), zag, b.getLocation(), (getActiveChest() == l && zag.getActiveMysteryChest() == null)));
            }
            for (Location l : getMobSpawnerLocations()) {
                MobSpawner zaloc = new MobSpawner(l, zag);
                zag.addObject(zaloc);
            }
            HashMap<Location, String> clays = getClaymoreLocations();
            for (Location l : clays.keySet()) {
                Player player = Bukkit.getPlayer(clays.get(l));
                if (player != null) {
                    ZAPlayer placer = data.getZAPlayer(player);
                    if (placer != null) {
                        Claymore more = new Claymore(l.getBlock(), zag, placer, false);
                        zag.addObject(more);
                    }
                }
            }
        } catch (Exception e) {
            Ablockalypse.crash("The previous game data failed to load to the game.", false);
            // nothing, the data not found will not be loaded
        }
    }

    public Location getActiveChest() {
        if (activechest != null)
            return SerializableLocation.returnLocation(activechest);
        return null;
    }

    public HashMap<Location, Location> getAreaPoints() {
        HashMap<Location, Location> save = new HashMap<Location, Location>();
        for (SerializableLocation sl : areapoints.keySet()) {
            SerializableLocation sl2 = areapoints.get(sl);
            Location l1 = SerializableLocation.returnLocation(sl);
            Location l2 = SerializableLocation.returnLocation(sl2);
            save.put(l1, l2);
        }
        return save;
    }

    public ArrayList<Location> getBarrierLocations() {
        ArrayList<Location> save = new ArrayList<Location>();
        for (SerializableLocation sl : barriers)
            save.add(SerializableLocation.returnLocation(sl));
        return save;
    }

    public int getLevel() {
        return level;
    }

    public Mainframe getMainframe(Game game) {
        if (mainframe != null) {
            Location loc = SerializableLocation.returnLocation(mainframe);
            return new Mainframe(game, loc);
        }
        return null;
    }

    public HashMap<Location, String> getClaymoreLocations() {
        HashMap<Location, String> save = new HashMap<Location, String>();
        for (SerializableLocation sl : claymores.keySet()) {
            save.put(SerializableLocation.returnLocation(sl), claymores.get(sl));
        }
        return save;
    }

    public ArrayList<Location> getMobSpawnerLocations() {
        ArrayList<Location> save = new ArrayList<Location>();
        for (SerializableLocation sl : spawns)
            save.add(SerializableLocation.returnLocation(sl));
        return save;
    }

    public ArrayList<Location> getMysteryChestLocations() {
        ArrayList<Location> save = new ArrayList<Location>();
        for (SerializableLocation sl : chests)
            save.add(SerializableLocation.returnLocation(sl));
        return save;
    }

    public String getName() {
        return name;
    }

    public ArrayList<PerPlayerDataStorage> getPlayerData() {
        return playerStorage;
    }

    public boolean isAreaOpen(Location loc1) {
        for (SerializableLocation sl : openedareas) {
            Location l = SerializableLocation.returnLocation(sl);
            if (loc1 == l)
                return true;
        }
        return false;
    }
}
