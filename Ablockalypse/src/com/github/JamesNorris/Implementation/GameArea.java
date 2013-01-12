package com.github.JamesNorris.Implementation;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.Setting;
import com.github.JamesNorris.Enumerated.ZAColor;
import com.github.JamesNorris.Enumerated.ZAEffect;
import com.github.JamesNorris.Enumerated.ZASound;
import com.github.JamesNorris.Interface.Area;
import com.github.JamesNorris.Interface.Blinkable;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.Rectangle;
import com.github.JamesNorris.Util.SoundUtil;

public class GameArea extends DataManipulator implements Area, GameObject, Blinkable {
	private BlinkerThread bt;
	private Location loc1, loc2;
	private HashMap<Location, Byte> locdata = new HashMap<Location, Byte>();
	private HashMap<Location, Material> locs = new HashMap<Location, Material>();
	private boolean opened, blinkers;
	private Rectangle rectangle;
	private ZAGameBase zag;

	/**
	 * Creates a new Area instance, that can be bought for a set price.
	 * 
	 * @param block The sign directly facing the area door
	 */
	@SuppressWarnings("deprecation") public GameArea(ZAGameBase zag, Location loc1, Location loc2) {
		data.objects.add(this);
		this.loc1 = loc1;
		this.loc2 = loc2;
		this.zag = zag;
		opened = false;
		data.areas.add(this);
		rectangle = new Rectangle(loc1, loc2);
		for (Location l : rectangle.getLocations()) {
			locs.put(l, l.getBlock().getType());
			locdata.put(l, l.getBlock().getData());
		}
		zag.addArea(this);
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (Location l : rectangle.get3DBorder())
			blocks.add(l.getBlock());
		this.blinkers = (Boolean) Setting.BLINKERS.getSetting();
		bt = new BlinkerThread(blocks, ZAColor.BLUE, blinkers, blinkers, 30, this);
	}

	/**
	 * Replaces the area.
	 */
	@Override public void close() {
		for (Location l : locs.keySet()) {
			Block b = l.getBlock();
			b.setType(locs.get(l));
			b.setData(locdata.get(l));
			EffectUtil.generateEffect(l.getWorld(), l, ZAEffect.SMOKE);
		}
		opened = false;
	}

	/**
	 * Gets the BlinkerThread attached to this instance.
	 * 
	 * @return The BlinkerThread attached to this instance
	 */
	@Override public BlinkerThread getBlinkerThread() {
		return bt;
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
	@SuppressWarnings("deprecation") @Override public ArrayList<Location> getBorderBlocks() {
		return rectangle.get3DBorder();
	}

	/**
	 * Gets the blocks that defines this object as an object.
	 * 
	 * @return The blocks assigned to this object
	 */
	@Override public ArrayList<Block> getDefiningBlocks() {
		ArrayList<Block> bs = new ArrayList<Block>();
		for (Location l : locs.keySet())
			bs.add(l.getBlock());
		return bs;
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
		Location loc = (i == 1) ? loc1 : loc2;
		return loc;
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
		data.areas.remove(this);
		data.objects.remove(this);
		data.blinkers.remove(bt);
		zag = null;
	}

	/**
	 * Stops/Starts the blinker for this area.
	 * 
	 * @param tf Whether or not this area should blink
	 */
	@Override public void setBlinking(boolean tf) {
		if (bt.isRunning())
			bt.cancel();
		if (tf)
			bt.blink();
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
		else if (n == 2)
			loc2 = loc;
		if (n == 1 || n == 2)
			rectangle = new Rectangle(loc1, loc2);
	}
}
