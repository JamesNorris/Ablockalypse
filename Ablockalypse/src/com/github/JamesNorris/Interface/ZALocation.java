package com.github.JamesNorris.Interface;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;

public interface ZALocation {
	public ZALocation add(double x, double y, double z);

	public BlinkerThread getBlinkerThread();

	public int getBlockX();

	public int getBlockY();

	public int getBlockZ();

	public Block getBukkitBlock();

	public Location getBukkitLocation();

	public double getX();

	public double getY();

	public double getZ();

	boolean isBlinking();

	public void playEffect(ZAEffect effect);

	public void remove();

	void setBlinking(boolean tf);

	public void setBlock(Material m);

	public void spawn(ZAMob mob);

	public ZALocation subtract(double x, double y, double z);
}
