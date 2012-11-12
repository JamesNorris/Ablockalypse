package com.github.JamesNorris.Interface;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Util.Square;

public interface Barrier extends GameObject {
	/**
	 * Slowly breaks the blocks of the barrier.
	 * 
	 * @param c The creature that is breaking the barrier
	 */
	public void breakBarrier(Creature c);

	/**
	 * Slowly breaks the blocks of the barrier.
	 * 
	 * @param e The entityliving that is breaking the barrier
	 */
	public void breakBarrier(LivingEntity e);

	/**
	 * Slowly breaks the blocks of the barrier.
	 * 
	 * @param p The player that is breaking the barrier
	 */
	public void breakBarrier(Player p);

	/**
	 * Changes all blocks within the barrier to air.
	 */
	public void breakPanels();

	/**
	 * Slowly fixes the blocks of the barrier.
	 * 
	 * @param c The creature that is fixing the barrier
	 */
	public void fixBarrier(Creature c);

	/**
	 * Slowly fixes the blocks of the barrier.
	 * 
	 * @param e The livingentity that is fixing the barrier
	 */
	public void fixBarrier(LivingEntity e);

	/**
	 * Slowly fixes the blocks of the barrier.
	 * 
	 * @param p The player that is fixing the barrier
	 */
	public void fixBarrier(Player p);

	/**
	 * Gets the BlinkerThread attached to this instance.
	 * 
	 * @return The BlinkerThread attached to this instance
	 */
	public BlinkerThread getBlinkerThread();

	/**
	 * Returns the list of blocks in the barrier.
	 * 
	 * @return A list of blocks located in the barrier
	 */
	public List<Block> getBlocks();

	/**
	 * Gets the center location of the barrier.
	 * 
	 * @return The center of the barrier
	 */
	public Location getCenter();

	/**
	 * Gets the game this barrier is involved in.
	 * 
	 * @return The game this barrier is attached to
	 */
	@Override public ZAGame getGame();

	/**
	 * Gets the radius of the barrier as an integer.
	 * 
	 * @return The radius of the barrier
	 */
	public int getRadius();

	/**
	 * Gets the mob spawn location for this barrier.
	 * 
	 * @return The mob spawn location around this barrier
	 */
	public Location getSpawnLocation();

	/**
	 * Gets the square surrounding this barrier for 2 blocks.
	 * 
	 * @return The barriers' surrounding square
	 */
	public Square getSquare();

	/**
	 * Checks if the BlinkerThread is running.
	 * 
	 * @return Whether or not the barrier is blinking
	 */
	public boolean isBlinking();

	/**
	 * Tells whether or not the barrier has any missing fence blocks.
	 * 
	 * @return Whether or not the barrier is broken
	 */
	public boolean isBroken();

	/**
	 * Checks if the barrier is setup correctly or not.
	 * 
	 * @return Whether or not the barrier is setup correctly
	 */
	public boolean isCorrect();

	/**
	 * Checks if the entity is within the radius of the barrier.
	 * 
	 * @param e The entity to check for
	 * @return Whether or not the entity is within the radius
	 */
	public boolean isWithinRadius(Entity e);

	/**
	 * Removes the barrier.
	 */
	@Override public void remove();

	/**
	 * Replaces all holes in the barrier.
	 */
	public void replacePanels();

	/**
	 * Stops/Starts the blinker for this barrier.
	 * 
	 * @param tf Whether or not this barrier should blink
	 */
	public void setBlinking(boolean tf);

	/**
	 * Sets the amount of fix rounds to wait before fixing the barrier.
	 * Fix rounds are called once every 20 ticks by all sneaking players near barriers.
	 * 
	 * @param i The amount of fix rounds to wait
	 */
	public void setFixRequirement(int i);

	/**
	 * Sets the amount of hit rounds to wait before breaking the barrier.
	 * Hit rounds are called once every 100 ticks by all mobs close to barriers.
	 * 
	 * @param i The amount of hit rounds to wait
	 */
	public void setHitRequirement(int i);

	/**
	 * Sets the radius of the barrier to be broken.
	 * 
	 * @param i The radius
	 */
	public void setRadius(int i);
}
