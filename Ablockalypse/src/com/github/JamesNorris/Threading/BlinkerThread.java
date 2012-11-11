package com.github.JamesNorris.Threading;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Interface.Area;
import com.github.JamesNorris.Interface.Barrier;
import com.github.JamesNorris.Util.Enumerated.ZAColor;

public class BlinkerThread {
	private int id, delay;
	private ZAColor color;
	private boolean colored, running;
	private Material type;
	private Block b;
	private Barrier barrier;
	private Area area;

	/**
	 * Creates a new thread that makes a block blink a colored wool.
	 * 
	 * @param b The block to flicker
	 * @param color The color to blink
	 * @param autorun Whether or not to automatically run the thread
	 * @param delay The delay between blinks
	 */
	public BlinkerThread(Block b, ZAColor color, final boolean autorun, int delay, Object type) {
		this.b = b;
		this.delay = delay;
		this.type = b.getType();
		this.color = color;
		if (type instanceof GameArea)
			area = (Area) type;
		else if (type instanceof GameBarrier)
			barrier = (Barrier) type;
		colored = false;
		id = -1;
		if (autorun)
			blink();
	}

	/**
	 * Makes the blinker blink in an alternating way.
	 */
	public void blink() {
		if (id == -1) {
			running = true;
			id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
				@Override public void run() {
					if (barrier != null && (barrier.getGame().hasStarted() && barrier.getGame().isPaused()))
						cancel();
					if (area != null && (area.getGame().hasStarted() && area.getGame().isPaused()))
						cancel();
					if (colored) {
						b.setType(type);
						colored = false;
					} else {
						b.setType(Material.WOOL);
						switch (color) {
							case RED:
								b.setData((byte) 14);
							break;
							case BLUE:
								b.setData((byte) 11);
							break;
							case GREEN:
								b.setData((byte) 5);
							break;
						}
						colored = true;
					}
				}
			}, delay, delay);
		} else
			Ablockalypse.getMaster().crash(Ablockalypse.instance, "A BlinkerThread has been told to run over the same repeating task, therefore this action has been cancelled to be safe.", false);
	}

	/**
	 * Cancels the thread.
	 */
	public void cancel() {
		running = false;
		b.setType(type);
		Bukkit.getScheduler().cancelTask(id);
		id = -1;
	}

	/**
	 * Checks if the thread is currently running.
	 * 
	 * @return Whether or not the thread is making a block blink
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Sets the color of the blinker.
	 * 
	 * @param color The color to blink
	 */
	public void setColor(ZAColor color) {
		this.color = color;
	}

	/**
	 * Sets the delay of the blinker.
	 * 
	 * @param delay The delay between blinks
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * Sets the block that re-appears after breakage.
	 * 
	 * @param type The type of the block
	 */
	public void setUnderlay(Material type) {
		this.type = type;
	}
}
