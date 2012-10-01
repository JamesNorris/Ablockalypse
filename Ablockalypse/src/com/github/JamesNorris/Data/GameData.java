package com.github.JamesNorris.Data;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.JamesNorris.External;

public class GameData {
	private List<String> games;

	/**
	 * Creates a new instance of GameData, and loads all strings from games.yml.
	 */
	public GameData() {
		FileConfiguration g = External.getConfig(External.g, External.games);
		games = g.getStringList("Current_ZA_Games");
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
