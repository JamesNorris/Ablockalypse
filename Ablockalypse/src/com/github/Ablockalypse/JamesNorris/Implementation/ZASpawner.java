package com.github.Ablockalypse.JamesNorris.Implementation;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Interface.ZASpawnerInterface;
import com.github.Ablockalypse.JamesNorris.Util.External;

public class ZASpawner implements ZASpawnerInterface {
	private final Block block;
	private final ConfigurationData cd = External.ym.getConfigurationData();
	private final ZAGame game;
	private final World world;

	/**
	 * Creates a new instance of a ZASpawner, which should be used to spawn entities from a specified mobspawner.
	 * 
	 * @param block The block this ZASpawner is attached to
	 * @param game The game this ZASpawner is involved in
	 */
	public ZASpawner(final Block block, final ZAGame game) {
		this.block = block;
		world = block.getWorld();
		this.game = game;
		final Location l = block.getLocation();
		if (!Data.spawners.containsValue(l))
			Data.spawners.put(game.getName(), l);
		if (!Data.loadedspawners.containsKey(l)) {
			Data.loadedspawners.put(l, this);
		}
	}

	/**
	 * Returns the block this spawner is located on.
	 * 
	 * @return The block this spawner is located on
	 */
	@Override public Block getBlock() {
		return block;
	}

	/**
	 * Returns the game that this spawner is running around.
	 * 
	 * @return The game this ZASpawner is involved in
	 */
	@Override public ZAGame getGame() {
		return game;
	}

	/**
	 * Returns the world that this spawner is in.
	 * 
	 * @return The world this spawner is in
	 */
	@Override public World getWorld() {
		return world;
	}

	/**
	 * Spawns an entity on top of the ZASpawner.
	 * NOTE: Should currently only be a zombie.
	 * NOTE: Entities spawned using this method will not be loaded into a game properly, instead, use ZAGame.addMob(spawner, game).
	 * 
	 * @param entity The EntityType to spawn
	 * @param game The game to spawn the entity for
	 */
	@Override public void spawnEntity(final EntityType entity, final ZAGame game) {
		final Entity e = world.spawnEntity(block.getLocation().add(0, 1, 0), entity);
		if (e instanceof Zombie) {
			final Zombie z = (Zombie) e;
			final GameZombie gz = new GameZombie(z);
			gz.setTarget(game.getRandomPlayer());
			gz.toggleFireImmunity();
			gz.setHealth(game.getLevel() * 4);// TODO make sure this is correct
			if (game.getLevel() >= cd.speedLevel)
				gz.increaseSpeed();
			if (cd.effects)
				world.playEffect(block.getLocation(), Effect.EXTINGUISH, 1);
		}
	}
}
