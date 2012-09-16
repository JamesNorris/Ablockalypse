package com.github.Ablockalypse.JamesNorris.Interface;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

public interface BarrierInterface {
	public void breakBarrier();

	public List<Block> getBlocks();

	public Location getCenter();

	public boolean isBroken();

	public void replaceBarrier();
}
