package com.github.Ablockalypse.JamesNorris.Interface;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public interface ZombieInterface {
	public World getWorld();

	public Zombie getZombie();

	public void increaseSpeed();

	public boolean isOnFire();

	public void setTarget(Player player);

	public void toggleFireImmunity();

	public void setHealth(int amt);
}
