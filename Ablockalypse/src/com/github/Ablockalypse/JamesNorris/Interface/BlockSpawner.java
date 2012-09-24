package com.github.Ablockalypse.JamesNorris.Interface;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

import com.github.Ablockalypse.JamesNorris.Implementation.ZAGameBase;

public interface BlockSpawner {
	public Block getBlock();

	public ZAGameBase getGame();

	public World getWorld();

	public void spawnEntity(EntityType entity, ZAGameBase game);
}
