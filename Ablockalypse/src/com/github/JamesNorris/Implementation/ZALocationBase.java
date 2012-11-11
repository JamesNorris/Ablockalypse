package com.github.JamesNorris.Implementation;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZALocation;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.Enumerated.ZAColor;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;

public class ZALocationBase implements ZALocation {// TODO annotations
	private Location loc;
	private Block block;
	private World world;
	private double X, Y, Z;
	private int x, y, z;
	private boolean spawner, blinkers;
	private ZAGame game;
	private BlinkerThread bt;

	public ZALocationBase(Location loc) {
		this.loc = loc;
		block = loc.getBlock();
		world = loc.getWorld();
		X = loc.getX();
		Y = loc.getY();
		Z = loc.getZ();
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		spawner = false;
		blinkers = External.getYamlManager().getConfigurationData().blinkers;
		bt = new BlinkerThread(block, ZAColor.BLUE, false, 60, this);
	}

	@Override public ZALocation add(double x, double y, double z) {
		loc = loc.add(x, y, z);
		return this;
	}

	@Override public BlinkerThread getBlinkerThread() {
		return bt;
	}

	@Override public int getBlockX() {
		return x;
	}

	@Override public int getBlockY() {
		return y;
	}

	@Override public int getBlockZ() {
		return z;
	}

	@Override public Block getBukkitBlock() {
		return block;
	}

	@Override public Location getBukkitLocation() {
		return loc;
	}

	@Override public double getX() {
		return X;
	}

	@Override public double getY() {
		return Y;
	}

	@Override public double getZ() {
		return Z;
	}

	/**
	 * Checks if the BlinkerThread is running.
	 * 
	 * @return Whether or not the location is blinking
	 */
	@Override public boolean isBlinking() {
		return bt.isRunning();
	}

	@Override public boolean isSpawner() {
		return spawner;
	}

	@Override public void playEffect(ZAEffect effect) {
		EffectUtil.generateEffect(world, loc, effect);
	}

	@Override public void remove() {
		if (isBlinking())
			setBlinking(false);
		for (ZAGameBase zag : Data.spawns.keySet()) {
			ZALocation loc = Data.spawns.get(zag);
			if (loc == this && loc.isSpawner())
				zag.removeMobSpawner(loc);
		}
	}

	/**
	 * Stops/Starts the blinker for this location.
	 * 
	 * @param tf Whether or not this location should blink
	 */
	@Override public void setBlinking(boolean tf) {
		bt.cancel();
		if (tf)
			bt.blink();
	}

	@Override public void setBlock(Material m) {
		block.setType(m);
	}

	@Override public void setSpawner(boolean tf, ZAGame game) {
		if (tf) {
			game.addMobSpawner(this);
			if (blinkers)
				bt.blink();
			spawner = true;
		} else {
			game.removeMobSpawner(this);
			if (blinkers)
			bt.cancel();
			spawner = false;
		}
	}

	@Override public void spawn(ZAMob mob) {
		if (spawner)
			game.getSpawnManager().spawn(loc, true);
	}

	@Override public ZALocation subtract(double x, double y, double z) {
		loc = loc.subtract(x, y, z);
		return this;
	}
}
