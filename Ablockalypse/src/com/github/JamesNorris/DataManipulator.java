package com.github.JamesNorris;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Manager.YamlManager;

public class DataManipulator {
	public GlobalData data = Ablockalypse.instance.data;
	public ConfigurationData cd = External.getYamlManager().getConfigurationData();
	public LocalizationData ld = External.getYamlManager().getLocalizationData();
	public YamlManager ym = External.getYamlManager();
}
