package com.github.JamesNorris.Threading;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.ZAColor;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Interface.GameObject;

public class BlinkerThread extends DataManipulator {
	private HashMap<Block, Byte> blockdata = new HashMap<Block, Byte>();
	private HashMap<Block, Material> blocks = new HashMap<Block, Material>();
	private ZAColor color;
	private boolean colored, running;
	private ZAGameBase game = null;
	private GameObject gameObj;
	private boolean gameSupport = game != null && gameObj.getGame() != null && game.hasStarted() && !game.isPaused() && data.blinkers.contains(this) && data.objects.contains(this);
	private int id, delay;
	private Object type;

	/**
	 * Creates a new thread that makes a block blink a colored wool.
	 * 
	 * @param b The blocks to flicker
	 * @param color The color to blink
	 * @param autorun Whether or not to automatically run the thread
	 * @param delay The delay between blinks
	 */
	public BlinkerThread(ArrayList<Block> blocks, ZAColor color, final boolean autorun, boolean synchronize, int delay, Object type) {
		data.blinkers.add(this);
		for (Block b : blocks) {
			this.blocks.put(b, b.getType());
			this.blockdata.put(b, b.getData());
		}
		this.delay = delay;
		this.color = color;
		this.type = type;
		if (type instanceof GameObject) {
			gameObj = ((GameObject) type);
			game = (ZAGameBase) gameObj.getGame();
		}
		colored = false;
		id = -1;
		if (synchronize && autorun)
			synchronize();
		else if (autorun)
			blink();
	}

	/**
	 * Makes the blinker blink in an alternating way.
	 */
	public void blink() {
		if (id == -1) {
			id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
				@Override public void run() {
					running = true;
					if (gameSupport)
						cancel();
					else {
						if (colored) {
							revertBlocks();
						} else {
							setBlocks(Material.WOOL);
							setBlocksData(color.getData());
						}
					}
				}
			}, delay, delay);
		} else
			Ablockalypse.crash("A BlinkerThread has been told to run over the same repeating task, therefore this action has been cancelled to maintain safety.", false);
	}

	/**
	 * Cancels the thread.
	 */
	public void cancel() {
		running = false;
		revertBlocks();
		Bukkit.getScheduler().cancelTask(id);
		id = -1;
	}

	/**
	 * Gets the associated type to this blinker.
	 * 
	 * @return The associated object
	 */
	public Object getAssociate() {
		return type;
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
	 * Reverts the blocks to original state.
	 */
	public void revertBlocks() {
		colored = false;
		for (Block b : blocks.keySet()) {
			b.setType(blocks.get(b));
			b.setData(blockdata.get(b));
		}
	}

	/**
	 * Sets the material of all blocks.
	 * 
	 * @param m The material to set the blocks to
	 */
	public void setBlocks(Material m) {
		for (Block b : blocks.keySet()) {
			if (m != b.getType())
				colored = true;
			b.setType(m);
		}
	}

	/**
	 * Sets the data of all blocks.
	 * 
	 * @param by The data of the blocks
	 */
	public void setBlocksData(byte by) {
		for (Block b : blocks.keySet()) {
			if (by != b.getData())
				colored = true;
			b.setData(by);
		}
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
	 * Synchronizes with all of the other BlinkerThreads, so that blinkers with equal waits will blink at the same time.
	 */
	public void synchronize() {
		for (BlinkerThread bt : data.blinkers) {
			if (!(gameSupport)) {
				bt.cancel();
				bt.blink();
			}
		}
	}
}
