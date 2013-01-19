package com.github.JamesNorris.Threading;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.ZAColor;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.ZAThread;

public class BlinkerThread extends DataManipulator implements ZAThread {
	private HashMap<Block, Byte> blockdata = new HashMap<Block, Byte>();
	private HashMap<Block, Material> blocks = new HashMap<Block, Material>();
	private ZAColor color;
	private boolean colored, running, runThrough;
	private ZAGameBase game = null;
	private GameObject gameObj;
	private boolean noGameSupport = game != null && gameObj.getGame() != null && game.hasStarted() && !game.isPaused() && data.blinkers.contains(this) && data.objects.contains(this);
	private Object type;
	private int interval, count = 0;

	/**
	 * Creates a new thread that makes a block blink a colored wool.
	 * 
	 * @param b The blocks to flicker
	 * @param color The color to blink
	 * @param autorun Whether or not to automatically run the thread
	 * @param interval The delay between blinks
	 */
	public BlinkerThread(ArrayList<Block> blocks, ZAColor color, final boolean autorun, int interval, Object type) {
		data.blinkers.add(this);
		for (Block b : blocks) {
			this.blocks.put(b, b.getType());
			this.blockdata.put(b, b.getData());
		}
		this.interval = interval;
		this.color = color;
		this.type = type;
		count = 0;
		if (type instanceof GameObject) {
			gameObj = ((GameObject) type);
			game = (ZAGameBase) gameObj.getGame();
		}
		colored = false;
		data.thread.add(this);
	}

	/**
	 * Makes the blinker blink in an alternating way.
	 */
	@Override public void run() {
			running = true;
			if (noGameSupport)
				remove();
			else {
				if (colored) {
					revertBlocks();
				} else {
					setBlocks(Material.WOOL);
					setBlocksData(color.getData());
				}
			}
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

	@Override public boolean runThrough() {
		return runThrough;
	}

	@Override public void setRunThrough(boolean tf) {
		this.runThrough = tf;
	}

	@Override public void remove() {
		runThrough = false;
		running = false;
		count = 0;
		revertBlocks();
	    data.thread.remove(this);
    }

	@Override public int getCount() {
	    return count;
    }

	@Override public int getInterval() {
	    return interval;
    }

	@Override public void setCount(int i) {
	    count = i;
    }

	@Override public void setInterval(int i) {
	    interval = i;
    }
}
