package com.github.Ablockalypse.JamesNorris.Interface;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

import com.github.Ablockalypse.JamesNorris.Implementation.ZAGame;

public interface ZASpawnerInterface {
	public Block getBlock();

	public ZAGame getGame();

	public World getWorld();

	public void spawnEntity(EntityType entity, ZAGame game);
}
