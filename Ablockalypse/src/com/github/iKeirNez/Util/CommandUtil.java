package com.github.iKeirNez.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.JamesNorris.DataManipulator;

public class CommandUtil extends DataManipulator {
	public static String joinGame = ChatColor.AQUA + "You have joined a game of Zombie Ablockalypse";
	public static String notPlayer = ChatColor.RED + "You must be a player to use that command.";
	public static String noMaintainPerms = ChatColor.RED + "You don't have permission to perform maintenance.";
	public static String settingChanged = ChatColor.GRAY + "That setting has been changed.";
	public static String invalidSetting = ChatColor.RED + "That setting does not exist!";

	/**
	 * Separates a collection into a comma separated list
	 * 
	 * @param inputArray An object to implode
	 * @param glueString The string to be separated
	 * @return The finalized string, separated
	 */
	public static String implode(final Object[] inputArray, final String glueString, final String finalGlueString) {
		String output = "";
		if (inputArray.length > 0) {
			final StringBuilder sb = new StringBuilder();
			sb.append(inputArray[0]);
			for (int i = 1; i < inputArray.length; i++) {
				if (i != inputArray.length - 1)
					sb.append(glueString);
				else
					sb.append(finalGlueString);
				sb.append(inputArray[i]);
			}
			output = sb.toString();
		}
		return output;
	}

	/**
	 * Lists all available games.
	 * 
	 * @param s CommandSender
	 */
	public void list(CommandSender s) {
		s.sendMessage(ChatColor.GOLD + "Available Games: " + ChatColor.YELLOW + implode(data.games.keySet().toArray(), ChatColor.GOLD + ", " + ChatColor.YELLOW, ChatColor.GOLD + " and " + ChatColor.YELLOW));
	}

	// Note: Max lines per view is 10
	/**
	 * Help, Setup, List, Info, Join, and Quit all displayed to the CommandSender.
	 * 
	 * @param s CommandSender
	 * @param alias The string input for the command
	 */
	public void showHelp(CommandSender s, String[] args, String alias) {
		String a = "/" + alias;
		ChatColor res = ChatColor.RESET;
		ChatColor g = ChatColor.GOLD;
		ChatColor b = ChatColor.BOLD;
		ChatColor gr = ChatColor.GRAY;
		ChatColor r = ChatColor.RED;
		if (args.length == 2 && args[1].equalsIgnoreCase("sign") && s.hasPermission("za.create")) {
			s.sendMessage(r + "----- Ablockalypse Sign Lines -----");
			s.sendMessage(r + ld.first + " " + gr + "This must be at the top of any ZA command sign");
			s.sendMessage(r + ld.joingame + " " + gr + "When clicked adds a player to a ZA game");
			s.sendMessage(r + ld.areastring + " " + gr + "When clicked unlocks a ZA area");
			s.sendMessage(r + ld.perkstring + " " + gr + "When clicked gives a perk");
			s.sendMessage(r + ld.enchstring + " " + gr + "When clicked enchants an item in your hand");
			s.sendMessage(r + ld.weaponstring + " " + gr + "When clicked gives a weapon");
			return;
		} else if (args.length == 2 && args[1].equalsIgnoreCase("setup") && s.hasPermission("za.create")) {
			s.sendMessage(r + "----- Ablockalypse Setup Help -----");
			s.sendMessage(g + a + b + " create <game> - " + res + gr + "Creates a new game");
			s.sendMessage(g + a + b + " barrier <game> - " + res + gr + "Creates a barrier for the game");
			s.sendMessage(g + a + b + " area <game> - " + res + gr + "Creates an area for the game");
			s.sendMessage(g + a + b + " spawner <game> - " + res + gr + "Creates a spawner for the game");
			s.sendMessage(g + a + b + " mainframe <game> - " + res + gr + "Sets the mainframe for the game");
			s.sendMessage(g + a + b + " chest <game> - " + res + gr + "Adds a mystery chest to the game");
			s.sendMessage(g + a + b + " remove <game> - " + res + gr + "Removes an entire game");
			s.sendMessage(g + a + b + " remove - " + res + gr + "Removes a barrier, area, or spawner");
		} else if (args.length == 2 && args[1].equalsIgnoreCase("settings") && s.hasPermission("za.create")) {
			s.sendMessage(r + "----- Ablockalypse Settings Help -----");
			s.sendMessage(g + a + b + " settings <game> - " + res + gr + "Shows the settings of a game");
			s.sendMessage(g + a + b + " settings <game> <setting> <boolean> - " + res + gr + "Changes a setting of a game");
			s.sendMessage(g + "" + b + "Settings: " + res + g + " FF (Friendly Fire)");
		} else {
			s.sendMessage(r + "----- Ablockalypse Help -----");
			s.sendMessage(g + a + b + " info - " + res + gr + "Shows info about Ablockalypse");
			s.sendMessage(g + a + b + " list - " + res + gr + "List all available arenas");
			s.sendMessage(g + a + b + " join <game> - " + res + gr + "Join a game of Zombie Ablockalypse");
			s.sendMessage(g + a + b + " quit - " + res + gr + "Quits a game of Zombie Ablockalypse");
			if (s.hasPermission("za.create")) {
				s.sendMessage(g + a + b + " help sign - " + res + gr + "Shows sign commandline requirements");
				s.sendMessage(g + a + b + " help setup - " + res + gr + "Shows how to setup games");
				s.sendMessage(g + a + b + " help settings - " + res + gr + "Shows changeable game settings");
			}
		}
	}
}
