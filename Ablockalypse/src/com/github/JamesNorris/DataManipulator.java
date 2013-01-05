package com.github.JamesNorris;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.GlobalData;

public class DataManipulator {
	public static GlobalData data = Ablockalypse.instance.data;

	public DataManipulator() {
		data = Ablockalypse.instance.data;
	}
}
