package com.github.JamesNorris.Data;

/* Breakable Packages */
import net.minecraft.server.v1_4_6.*;
/* End Breakable Packages */

import java.util.ArrayList;

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
