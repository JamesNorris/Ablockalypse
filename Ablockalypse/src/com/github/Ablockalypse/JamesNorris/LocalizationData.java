package com.github.Ablockalypse.JamesNorris;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.Ablockalypse.Ablockalypse;

public class LocalizationData {
	public LocalizationData(Ablockalypse instance) {
		FileConfiguration c = instance.getConfig();// TODO get all values from the local.yml
	}
}
