package com.github.JamesNorris;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Manager.YamlManager;

public class DataManipulator {
	public static GlobalData data = Ablockalypse.instance.data;
	public static ConfigurationData cd;
	public static LocalizationData ld;
	public static YamlManager ym;
	public static PackageLoader pl;

	public DataManipulator() {
		data = Ablockalypse.instance.data;
		cd = External.getYamlManager().getConfigurationData();
		ld = External.getYamlManager().getLocalizationData();
		ym = External.getYamlManager();
		pl = Ablockalypse.getPackageLoader();
	}
}
