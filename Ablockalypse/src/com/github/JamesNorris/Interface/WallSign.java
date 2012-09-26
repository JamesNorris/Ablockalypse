package com.github.JamesNorris.Interface;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public interface WallSign {
	public String getLine(int number);

	public Location getLocation();

	public Sign getSign();

	public World getWorld();

	void runLines(Player player);
}
