package com.github.Ablockalypse.JamesNorris.Interface;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface WolfInterface {
	public void addEffect();

	public void addHealth(int amt);

	public World getWorld();

	public void increaseSpeed();

	public void setAggressive(boolean tf);

	public void setTarget(Player player);
}
