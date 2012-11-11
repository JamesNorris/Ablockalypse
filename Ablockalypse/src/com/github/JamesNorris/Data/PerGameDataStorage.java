package com.github.JamesNorris.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Interface.MysteryChest;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZALocation;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Util.SerializableLocation;

public class PerGameDataStorage implements Serializable {//TODO annotations
	private static final long serialVersionUID = 7825383085566172198L;
	private final String name;
	private SerializableLocation activechest = null;
	private final SerializableLocation mainframe;
	private final ArrayList<SerializableLocation> chests = new ArrayList<SerializableLocation>();
	private final ArrayList<SerializableLocation> barriers = new ArrayList<SerializableLocation>(); 
	private final ArrayList<SerializableLocation> spawns = new ArrayList<SerializableLocation>();
	private final ArrayList<SerializableLocation> openedareas = new ArrayList<SerializableLocation>();
	private final int level;
	private final HashMap<String, Integer> points = new HashMap<String, Integer>();
	private final HashMap<SerializableLocation, SerializableLocation> areapoints = new HashMap<SerializableLocation, SerializableLocation>();
	
	public PerGameDataStorage(ZAGame game) {
		name = game.getName();
		if (game.getActiveMysteryChest() != null)
		activechest = new SerializableLocation(game.getActiveMysteryChest().getLocation());
		for (MysteryChest mc : game.getMysteryChests())
			chests.add(new SerializableLocation(mc.getLocation()));
		mainframe = new SerializableLocation(game.getMainframe());
		level = game.getLevel();
		for (String s : game.getPlayers()) {
			Player p = Bukkit.getPlayer(s);
			ZAPlayer zap = Data.getZAPlayer(p);
			points.put(zap.getName(), zap.getPoints());
		}
		for (GameBarrier gb : game.getBarriers())
			barriers.add(new SerializableLocation(gb.getCenter()));
		for (GameArea ga : game.getAreas()) {
			SerializableLocation point1 = new SerializableLocation(ga.getPoint(1));
			areapoints.put(point1, new SerializableLocation(ga.getPoint(2)));
			if (ga.isOpened())
				openedareas.add(point1);
		}
		for (ZALocation l : game.getMobSpawners())
			spawns.add(new SerializableLocation(l.getBukkitLocation()));
	}
	
	public String getName() {
		return name;
	}
	
	public Location getActiveChest() {
		return SerializableLocation.returnLocation(activechest);
	}
	
	public Location getMainframe() {
		return SerializableLocation.returnLocation(mainframe);
	}
	
	public int getLevel() {
		return level;
	}
	
	public ArrayList<Location> getMysteryChestLocations() {
		ArrayList<Location> save = new ArrayList<Location>();
		for (SerializableLocation sl : chests)
			save.add(SerializableLocation.returnLocation(sl));
		return save;
	}
	
	public ArrayList<Location> getBarrierLocations() {
		ArrayList<Location> save = new ArrayList<Location>();
		for (SerializableLocation sl : barriers)
			save.add(SerializableLocation.returnLocation(sl));
		return save;
	}
	
	public ArrayList<Location> getMobSpawnerLocations() {
		ArrayList<Location> save = new ArrayList<Location>();
		for (SerializableLocation sl : spawns)
			save.add(SerializableLocation.returnLocation(sl));
		return save;
	}
	
	public ArrayList<String> getPlayers() {
		ArrayList<String> save = new ArrayList<String>();
		for (String s : points.keySet())
			save.add(s);
		return save;
	}
	
	public HashMap<String, Integer> getPlayerPoints() {
		return points;
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
	
	public boolean isAreaOpen(Location loc1) {
		for (SerializableLocation sl : openedareas) {
			Location l = SerializableLocation.returnLocation(sl);
			if (loc1 == l)
				return true;
		}
		return false;
	}
}
