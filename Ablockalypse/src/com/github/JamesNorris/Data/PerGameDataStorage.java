package com.github.JamesNorris.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.MysteryChest;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZALocation;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Util.SerializableLocation;

public class PerGameDataStorage implements Serializable {// TODO annotations
	private static final long serialVersionUID = 7825383085566172198L;
	private final String name;
	private SerializableLocation activechest = null, mainframe = null;
	private final ArrayList<SerializableLocation> chests = new ArrayList<SerializableLocation>();
	private final ArrayList<SerializableLocation> barriers = new ArrayList<SerializableLocation>();
	private final ArrayList<SerializableLocation> spawns = new ArrayList<SerializableLocation>();
	private final ArrayList<SerializableLocation> openedareas = new ArrayList<SerializableLocation>();
	private final int level;
	private final ArrayList<PerPlayerDataStorage> playerStorage = new ArrayList<PerPlayerDataStorage>();
	private final HashMap<SerializableLocation, SerializableLocation> areapoints = new HashMap<SerializableLocation, SerializableLocation>();

	public PerGameDataStorage(ZAGame game) {
		name = game.getName();
		if (game.getActiveMysteryChest() != null)
			activechest = new SerializableLocation(game.getActiveMysteryChest().getLocation());
		for (MysteryChest mc : game.getMysteryChests())
			chests.add(new SerializableLocation(mc.getLocation()));
		Location mf = game.getMainframe();
		if (mf != null)
			mainframe = new SerializableLocation(mf);
		level = game.getLevel();
		for (String s : game.getPlayers()) {
			Player p = Bukkit.getPlayer(s);
			ZAPlayer zap = GlobalData.getZAPlayer(p);
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
		return SerializableLocation.returnLocation(mainframe);
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
