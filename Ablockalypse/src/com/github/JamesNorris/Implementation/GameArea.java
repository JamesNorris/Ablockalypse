package com.github.JamesNorris.Implementation;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.Area;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.EffectUtil.ZAEffect;
import com.github.JamesNorris.Util.SoundUtil;
import com.github.JamesNorris.Util.SoundUtil.ZASound;

public class GameArea implements Area {
	private Location loc1, loc2;
	private HashMap<Location, Material> locs = new HashMap<Location, Material>();
	private boolean opened;
	private ZAGameBase zag;

	/**
	 * Creates a new Area instance, that can be bought for a set price.
	 * 
	 * @param block The sign directly facing the area door
	 */
	public GameArea(ZAGameBase zag) {
		this.zag = zag;
		opened = false;
		Data.areas.add(this);
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
	 * Sets the first or second location of the area.
	 * 
	 * @param loc The location to set
	 * @param n A number between 1 and 2
	 */
	@Override public void setLocation(Location loc, int n) {
		if (n == 1)
			loc1 = loc;
		if (n == 2) {
			loc2 = loc;
			if (loc1 == null) {
				Ablockalypse.getMaster().crash(Ablockalypse.instance, "An area has been selected without a first location", false);
			} else {
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
				for (int i = modX; i <= highX; i++) {
					for (int j = modY; j <= highY; j++) {
						for (int k = modZ; k <= highZ; k++) {
							Location l = loc1.getWorld().getBlockAt(i, j, k).getLocation();
							locs.put(l, l.getBlock().getType());
						}
					}
				}
			}
		}
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
}
