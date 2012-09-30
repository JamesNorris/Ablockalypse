package com.github.JamesNorris.Data;

import java.util.ArrayList;

import net.minecraft.server.DataWatcher;
import net.minecraft.server.WatchableObject;

public class ByteData extends DataWatcher {
	private byte data;

	public ByteData(byte data) {
		this.data = data;
	}

	@Override public ArrayList<WatchableObject> b() {
		ArrayList<WatchableObject> list = new ArrayList<WatchableObject>();
		list.add(new WatchableObject(0, 0, data));
		return list;
	}
}
