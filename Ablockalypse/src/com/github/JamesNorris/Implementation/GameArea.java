package com.github.JamesNorris.Implementation;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Interface.Area;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.Enumerated.ZAColor;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;
import com.github.JamesNorris.Util.Enumerated.ZASound;
import com.github.JamesNorris.Util.SoundUtil;

public class GameArea implements Area, GameObject {
	private Location loc1, loc2;
	private HashMap<Location, Material> locs = new HashMap<Location, Material>();
	private boolean opened;
	private ZAGameBase zag;
	private ArrayList<Location> border = new ArrayList<Location>();
	private ArrayList<BlinkerThread> bts = new ArrayList<BlinkerThread>();

	/**
	 * Creates a new Area instance, that can be bought for a set price.
	 * 
	 * @param block The sign directly facing the area door
	 */
	public GameArea(ZAGameBase zag, Location loc1, Location loc2) {
		GlobalData.objects.add(this);
		this.zag = zag;
		opened = false;
		GlobalData.areas.add(this);
		calculateRectangle(loc1, loc2);
		zag.addArea(this);
		if (External.getYamlManager().getConfigurationData().blinkers)
			for (Location l : border) {
				BlinkerThread bt = new BlinkerThread(l.getBlock(), ZAColor.BLUE, true, 30, this);
				bts.add(bt);
			}
	}

	/*
	 * Calculates the rectangular area.
	 */
	private void calculateRectangle(Location loc1, Location loc2) {
		this.loc1 = loc1;
		this.loc2 = loc2;
		if (loc1 == null && loc2 != null)
			Ablockalypse.getMaster().crash(Ablockalypse.instance, "An area has been selected without a two locations.", false);
		else {
			int x = loc1.getBlockX();
			int y = loc1.getBlockY();
			int z = loc1.getBlockZ();
			int x2 = loc2.getBlockX();
			int y2 = loc2.getBlockY();
			int z2 = loc2.getBlockZ();
			int modX = 0;
			int highX = 0;
			if (x < x2) {
				modX = x;
				highX = x2;
			} else {
				modX = x2;
				highX = x;
			}
			int modY = 0;
			int highY = 0;
			if (y < y2) {
				modY = y;
				highY = y2;
			} else {
				modY = y2;
				highY = y;
			}
			int modZ = 0;
			int highZ = 0;
			if (z < z2) {
				modZ = z;
				highZ = z2;
			} else {
				modZ = z2;
				highZ = z;
			}
			for (int i = modX; i <= highX; i++)
				for (int j = modY; j <= highY; j++)
					for (int k = modZ; k <= highZ; k++) {
						Location l = loc1.getWorld().getBlockAt(i, j, k).getLocation();
						locs.put(l, l.getBlock().getType());
						GlobalData.removallocs.put(l, this);
					}
			// border
			// loc1
			for (int i = modX; i <= highX; i++) {
				Location l = loc1.getWorld().getBlockAt(i, y, z).getLocation();
				border.add(l);
			}
			for (int j = modY; j <= highY; j++) {
				Location l = loc1.getWorld().getBlockAt(x, j, z).getLocation();
				border.add(l);
			}
			for (int k = modZ; k <= highZ; k++) {
				Location l = loc1.getWorld().getBlockAt(x, y, k).getLocation();
				border.add(l);
			}
			// loc2
			for (int i = modX; i <= highX; i++) {
				Location l = loc1.getWorld().getBlockAt(i, y2, z2).getLocation();
				border.add(l);
			}
			for (int j = modY; j <= highY; j++) {
				Location l = loc1.getWorld().getBlockAt(x2, j, z2).getLocation();
				border.add(l);
			}
			for (int k = modZ; k <= highZ; k++) {
				Location l = loc1.getWorld().getBlockAt(x2, y2, k).getLocation();
				border.add(l);
			}
		}
	}

	/**
	 * Replaces the area.
	 */
	@Override public void close() {
		for (Location l : locs.keySet()) {
			l.getBlock().setType(locs.get(l));
			EffectUtil.generateEffect(l.getWorld(), l, ZAEffect.SMOKE);
		}
		opened = false;
	}

	/**
	 * Gets all BlinkerThreads attached to this instance.
	 * 
	 * @return The BlinkerThreads attached to this instance
	 */
	@Override public ArrayList<BlinkerThread> getBlinkerThreads() {
		return bts;
	}

	/**
	 * Gets a list of blocks for this area.
	 * 
	 * @return A list of blocks for this area
	 */
	@Override public ArrayList<Block> getBlocks() {
		ArrayList<Block> bls = new ArrayList<Block>();
		for (Location l : locs.keySet())
			bls.add(l.getBlock());
		return bls;
	}

	/**
	 * Gets the blocks around the border of the Area.
	 * 
	 * @return The blocks around the border
	 */
	@Override public ArrayList<Location> getBorderBlocks() {
		return border;
	}

	/**
	 * Gets the game this area is assigned to.
	 * 
	 * @return The game this area is assigned to
	 */
	@Override public ZAGameBase getGame() {
		return zag;
	}

	/**
	 * Gets a point from the area. This must be between 1 and 2.
	 * 
	 * @param i The point to get
	 * @return The location of the point
	 */
	@Override public Location getPoint(int i) {
		if (i == 1)
			return loc1;
		if (i == 2)
			return loc2;
		return null;
	}

	/**
	 * Checks if the BlinkerThreads are running.
	 * 
	 * @return Whether or not the area is blinking
	 */
	@Override public boolean isBlinking() {
		return bts.get(1).isRunning();
	}

	/**
	 * Returns if the area is opened or not.
	 * 
	 * @return Whether or not the area has been opened
	 */
	@Override public boolean isOpened() {
		return opened;
	}

	/**
	 * Removes the area.
	 */
	@Override public void open() {
		for (Location l : locs.keySet()) {
			l.getBlock().getDrops().clear();
			l.getBlock().setType(Material.AIR);
			EffectUtil.generateEffect(l.getWorld(), l, ZAEffect.SMOKE);
		}
		opened = true;
		SoundUtil.generateSound(loc1.getWorld(), loc1, ZASound.AREA_BUY);
	}

	/**
	 * Removes the area.
	 */
	@Override public void remove() {
		close();
		setBlinking(false);
		GlobalData.areas.remove(this);
		GlobalData.objects.remove(this);
		for (Location l : locs.keySet())
			GlobalData.removallocs.remove(l);
	}

	/**
	 * Stops/Starts the blinker for this area.
	 * 
	 * @param tf Whether or not this area should blink
	 */
	@Override public void setBlinking(boolean tf) {
		for (BlinkerThread bt : bts) {
			bt.cancel();
			if (tf)
				bt.blink();
		}
	}

	/**
	 * Sets the first or second location of the area.
	 * 
	 * @param loc The location to set
	 * @param n A number between 1 and 2
	 */
	@Override public void setLocation(Location loc, int n) {
		if (n == 1)
			loc1 = loc;
		calculateRectangle(loc1, loc2);
		if (n == 2) {
			loc2 = loc;
			calculateRectangle(loc1, loc2);
		}
	}
}
