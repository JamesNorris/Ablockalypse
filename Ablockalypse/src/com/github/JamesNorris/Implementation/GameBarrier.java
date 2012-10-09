package com.github.JamesNorris.Implementation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.Barrier;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.EffectUtil.ZAEffect;
import com.github.JamesNorris.Util.MathAssist;
import com.github.JamesNorris.Util.SoundUtil;
import com.github.JamesNorris.Util.SoundUtil.ZASound;
import com.github.JamesNorris.Util.Square;

public class GameBarrier implements Barrier {
	private ArrayList<Block> blocks = new ArrayList<Block>();
	private Location center, spawnloc;
	private int id, hittimes, radius;
	private ZAGameBase game;
	private Square square;

	/**
	 * Creates a new instance of a Barrier, where center is the center of the 3x3 barrier.
	 * 
	 * @param center The center of the barrier
	 */
	public GameBarrier(Block center, ZAGameBase game) {
		this.center = center.getLocation();
		this.game = game;
		this.radius = 2;
		this.hittimes = 5;
		square = new Square(getCenter(), 2);
		for (Location loc : square.getLocations()) {
			Material type = loc.getBlock().getType();
			if (loc.getBlock() != null && !loc.getBlock().isEmpty() && type != null && (type == Material.GRASS || type == Material.DIRT || type == Material.MYCEL)) {
				this.spawnloc = loc;
				break;
			}
		}
		if (spawnloc == null)
			Ablockalypse.getMaster().crash(Ablockalypse.instance, "A barrier has been created that doesn't have a suitable mob spawn location nearby. This could cause NullPointerExceptions in the future!", false);
		game.addBarrier(this);
		Location l = center.getLocation();
		if (!Data.barriers.containsKey(l))
			Data.barriers.put(center.getLocation(), game.getName());
		if (!Data.gamebarriers.contains(this))
			Data.gamebarriers.add(this);
		Square s = new Square(center.getLocation(), 1);
		for (Location loc : s.getLocations()) {
			Block b = loc.getBlock();
			if (b != null && !b.isEmpty() && b.getType() != null && b.getType() == Material.FENCE) {
				blocks.add(b);
				if (!Data.barrierpanels.containsValue(loc))
					Data.barrierpanels.put(this, loc);
			}
		}
	}

	/**
	 * Gets the square surrounding this barrier for 2 blocks.
	 * 
	 * @return The barriers' surrounding square
	 */
	@Override public Square getSquare() {
		return square;
	}

	/**
	 * Gets the game this barrier is involved in.
	 * 
	 * @return The game this barrier is attached to
	 */
	@Override public ZAGame getGame() {
		return game;
	}

	/**
	 * Changes all blocks within the barrier to air.
	 * 
	 * @param c The creature that is breaking the barrier
	 */
	@Override public void breakBarrier(final Creature c) {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
			public void run() {
				if (!c.isDead() && withinRadius((Entity) c) && !isBroken()) {
					--hittimes;
					SoundUtil.generateSound(center.getWorld(), center, ZASound.BARRIER_BREAK);
					EffectUtil.generateEffect(c.getWorld(), center, ZAEffect.WOOD_BREAK);
					if (hittimes == 0) {
						hittimes = 5;
						breakPanels();
						if (game.getRandomLivingPlayer() != null)
							Data.getZAMob((Entity) c).setTargetPlayer(game.getRandomLivingPlayer());
						cancel();
					}
				} else {
					cancel();
				}
			}
		}, 100, 100);
	}

	/**
	 * Sets the radius of the barrier to be broken.
	 * 
	 * @param i The radius
	 */
	public void setRadius(int i) {
		this.radius = i;
	}

	/**
	 * Gets the radius of the barrier as an integer.
	 * 
	 * @return The radius of the barrier
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * Checks if the entity is within the radius of the barrier.
	 * 
	 * @param e The entity to check for
	 * @return Whether or not the entity is within the radius
	 */
	@Override public boolean withinRadius(Entity e) {
		Location el = e.getLocation();
		int x = el.getBlockX(), x2 = center.getBlockX();
		int y = el.getBlockY(), y2 = center.getBlockY();
		int z = el.getBlockZ(), z2 = center.getBlockZ();
		int distance = (int) MathAssist.distance(x, y, z, x2, y2, z2);
		if (distance <= radius)
			return true;
		return false;
	}

	/*
	 * Cancels the breakbarrier task.
	 */
	private void cancel() {
		Bukkit.getScheduler().cancelTask(id);
	}

	/**
	 * Returns the list of blocks in the barrier.
	 * 
	 * @return A list of blocks located in the barrier
	 */
	@Override public List<Block> getBlocks() {
		return blocks;
	}

	/**
	 * Gets the center location of the barrier.
	 * 
	 * @return The center of the barrier
	 */
	@Override public Location getCenter() {
		return center;
	}

	/**
	 * Tells whether or not the barrier has any missing fence blocks.
	 * 
	 * @return Whether or not the barrier is broken
	 */
	@Override public boolean isBroken() {
		if (center.getBlock().getType() != Material.FENCE)
			return true;
		return false;
	}

	/**
	 * Replaces all holes in the barrier.
	 */
	@Override public void replaceBarrier() {
		for (int i = 0; i <= 9; i++) {
			Block b = blocks.iterator().next();
			blocks.remove(b);
			b.setType(Material.FENCE);
			EffectUtil.generateEffect(b.getWorld(), b.getLocation(), ZAEffect.SMOKE);
			blocks.add(b);
		}
		SoundUtil.generateSound(center.getWorld(), center, ZASound.BARRIER_REPAIR);
	}

	/**
	 * Gets the mob spawn location for this barrier.
	 * 
	 * @return The mob spawn location around this barrier
	 */
	@Override public Location getSpawnLocation() {
		return spawnloc;
	}

	/**
	 * Changes all blocks within the barrier to air.
	 */
	@Override public void breakPanels() {
		for (int i = 0; i <= 9; i++) {
			Block b = blocks.iterator().next();
			blocks.remove(b);
			b.setType(Material.AIR);
			EffectUtil.generateEffect(b.getWorld(), b.getLocation(), ZAEffect.SMOKE);
			blocks.add(b);
		}
	}
}
