package com.github.Ablockalypse.JamesNorris.Data;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.Ablockalypse.JamesNorris.Util.External;

public class GameData {
	private List<String> games;

	/**
	 * Creates a new instance of GameData, and loads all strings from games.yml.
	 */
	public GameData() {
		FileConfiguration g = External.getConfig(External.g, External.games);
		games = g.getStringList("Current ZA Games");
	}

	/**
	 * Gets a list of games that were in the games.yml file on load.
	 * 
	 * @return A list of saved games
	 */
	public List<String> getSavedGames() {
		return games;
	}
}
