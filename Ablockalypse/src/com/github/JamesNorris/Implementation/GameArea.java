package com.github.JamesNorris.Implementation;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Interface.Area;
import com.github.JamesNorris.Interface.Blinkable;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.Enumerated.ZAColor;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;
import com.github.JamesNorris.Util.Enumerated.ZASound;
import com.github.JamesNorris.Util.Rectangle;
import com.github.JamesNorris.Util.SoundUtil;

public class GameArea implements Area, GameObject, Blinkable {
	private Location loc1, loc2;
	private HashMap<Location, Material> locs = new HashMap<Location, Material>();
	private HashMap<Location, Byte> locdata = new HashMap<Location, Byte>();
	private boolean opened;
	private ZAGameBase zag;
	private BlinkerThread bt;
	private Rectangle rectangle;

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
		rectangle = new Rectangle(loc1, loc2);
		for (Location l : rectangle.getLocations()) {
			locs.put(l, l.getBlock().getType());
			locdata.put(l, l.getBlock().getData());
		}
		zag.addArea(this);
		if (External.getYamlManager().getConfigurationData().blinkers) {
			ArrayList<Block> blocks = new ArrayList<Block>();
			for (Location l : rectangle.get2DBorder())
				blocks.add(l.getBlock());
			bt = new BlinkerThread(blocks, ZAColor.BLUE, false, 30, this);
		}
	}

	/**
	 * Gets the blocks that defines this object as an object.
	 * 
	 * @return The blocks assigned to this object
	 */
	public Block[] getDefiningBlocks() {
		ArrayList<Block> bs = new ArrayList<Block>();
		for (Location l : locs.keySet()) {
			bs.add(l.getBlock());
		}
		return (Block[]) bs.toArray();
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
	@Override public ArrayList<Location> getBorderBlocks() {
		return rectangle.get2DBorder();
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
		// setBlinking(false);
		GlobalData.areas.remove(this);
		GlobalData.objects.remove(this);
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
