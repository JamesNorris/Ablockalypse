package com.github.Ablockalypse.JamesNorris.Data;

import java.util.ArrayList;

import net.minecraft.server.DataWatcher;
import net.minecraft.server.WatchableObject;

public class ByteData extends DataWatcher {
	private byte metadata;

	/**
	 * Creates a new instance of ByteData, which extends DataWatcher.
	 * 
	 * @param metadata A hex byte
	 */
	public ByteData(byte metadata) {
		this.metadata = metadata;
	}

	/*
	 * Hopefully this will not break anytime soon. This is an extension of DataWatcher from net.minecraft.server.
	 * If this does break, this should simply have the method name changed.
	 * This currently the only breakable extension of NMS is Ablockalypse.
	 * 
	 * (non-Javadoc)
	 * @see net.minecraft.server.DataWatcher#b()
	 */
	@Override public ArrayList<WatchableObject> b() {
		ArrayList<WatchableObject> list = new ArrayList<WatchableObject>();
		WatchableObject wo = new WatchableObject(0, 0, metadata);
		list.add(wo);
		return list;
	}
}
