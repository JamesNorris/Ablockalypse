package com.github.Ablockalypse.JamesNorris.Interface;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public interface ZASignInterface {
	public String getLine(int number);

	public Sign getSign();

	public Location getLocation();

	public World getWorld();

	void runLines(Player player);
}
