package com.github.JamesNorris.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Event.Bukkit.PlayerJoin;
import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameMobSpawner;
import com.github.JamesNorris.Implementation.GameMysteryChest;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.MysteryChest;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZALocation;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Util.SerializableLocation;

public class PerGameDataStorage implements Serializable {// TODO annotations
    private static final long serialVersionUID = 7825383085566172198L;
    private final SerializableLocation activechest, mainframe;
    private final HashMap<SerializableLocation, SerializableLocation> areapoints = new HashMap<SerializableLocation, SerializableLocation>();
    private final ArrayList<SerializableLocation> barriers = new ArrayList<SerializableLocation>();
    private final ArrayList<SerializableLocation> chests = new ArrayList<SerializableLocation>();
    private final int level;
    private final String name;
    private final ArrayList<SerializableLocation> openedareas = new ArrayList<SerializableLocation>();
    private final ArrayList<PerPlayerDataStorage> playerStorage = new ArrayList<PerPlayerDataStorage>();
    private final ArrayList<SerializableLocation> spawns = new ArrayList<SerializableLocation>();

    public PerGameDataStorage(ZAGame game) {
        name = game.getName();
        if (game.getActiveMysteryChest() != null)
            activechest = new SerializableLocation(game.getActiveMysteryChest().getLocation());
        else
            activechest = null;
        for (MysteryChest mc : game.getMysteryChests())
            chests.add(new SerializableLocation(mc.getLocation()));
        Location mf = game.getMainframe();
        mainframe = (mf != null) ? new SerializableLocation(mf) : null;
        level = game.getLevel();
        for (String s : game.getPlayers()) {
            Player p = Bukkit.getPlayer(s);
            Ablockalypse.getData();
            ZAPlayer zap = DataManipulator.data.getZAPlayer(p);
            playerStorage.add(new PerPlayerDataStorage((ZAPlayerBase) zap));
        }
        for (GameBarrier gb : game.getBarriers())
            barriers.add(new SerializableLocation(gb.getCenter()));
        for (GameArea ga : game.getAreas()) {
            Location pt1 = ga.getPoint(1);
            Location pt2 = ga.getPoint(2);
            if (pt1 != null && pt2 != null) {
                SerializableLocation point1 = new SerializableLocation(pt1);
                areapoints.put(point1, new SerializableLocation(pt2));
                if (ga.isOpened())
                    openedareas.add(point1);
            }
        }
        for (ZALocation l : game.getMobSpawners())
            spawns.add(new SerializableLocation(l.getBukkitLocation()));
    }
    
    public void load(GlobalData data) {
        String name = getName();
        ZAGame zag = data.findGame(name);
        if (getMainframe() != null)
            zag.setMainframe(getMainframe());
        int level = getLevel();
        int setLevel = (zag.getPlayers().size() > 0) ? level : 0;
        zag.setLevel(setLevel);
        for (PerPlayerDataStorage spds : getPlayerData()) {
            Player p = Bukkit.getPlayer(spds.getName());
            Ablockalypse.getData();
            if (!DataManipulator.data.playerExists(p))
                new ZAPlayerBase(p, data.findGame(spds.getGameName()));
            if (p.isOnline() && data.playerExists(p)) {
                ZAPlayerBase zap = (ZAPlayerBase) data.getZAPlayer(p);
                if (zag.getLevel() < spds.getGameLevel()) {
                    zag.setLevel(spds.getGameLevel());
                    spds.loadToPlayer(zap);
                }
            } else {
                PlayerJoin.offlinePlayers.put(p.getName(), spds);
            }
        }
        for (Location l : getBarrierLocations())
            new GameBarrier(l.getBlock(), (ZAGameBase) data.findGame(name));
        for (Location l : getAreaPoints().keySet()) {
            Location l2 = getAreaPoints().get(l);
            if (l2 != null) {
                GameArea a = new GameArea((ZAGameBase) zag, l, l2);
                if (isAreaOpen(l))
                    a.open();
            }
        }
        for (Location l : getMysteryChestLocations()) {
            Block b = l.getBlock();
            zag.addMysteryChest(new GameMysteryChest(b.getState(), zag, b.getLocation(), (getActiveChest() == l && zag.getActiveMysteryChest() == null)));
        }
        for (Location l : getMobSpawnerLocations()) {
            GameMobSpawner zaloc = new GameMobSpawner(l, zag);
            zag.addMobSpawner(zaloc);
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

    public Location getMainframe() {
        if (mainframe != null)
            return SerializableLocation.returnLocation(mainframe);
        return null;
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
