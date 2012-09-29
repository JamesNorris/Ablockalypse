package com.github.iKeirNez.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Data.LocalizationData;

public class CommandUtil {
	/**
	 * Lists all available games.
	 * 
	 * @param s CommandSender
	 */
	public static void list(CommandSender s) {
		s.sendMessage(ChatColor.GOLD + "Available Games: " + ChatColor.YELLOW + StringFunctions.implode(Data.games.keySet().toArray(), ChatColor.GOLD + ", " + ChatColor.YELLOW, ChatColor.GOLD + " and " + ChatColor.YELLOW));
	}

	// Can you show the strings for signs from the config.yml as well?
	/**
	 * Help, Setup, List, Info, Join, and Quit all displayed to the CommandSender.
	 * 
	 * @param s CommandSender
	 * @param alias The string input for the command
	 */
	public static void showHelp(CommandSender s, String[] args, String alias) {
		LocalizationData ld = External.ym.getLocalizationData();
		String a = "/" + alias;
		ChatColor r = ChatColor.RESET;
		ChatColor g = ChatColor.GOLD;
		ChatColor b = ChatColor.BOLD;
		ChatColor y = ChatColor.YELLOW;
		ChatColor aq = ChatColor.AQUA;
		if (args.length == 2 && args[1].equalsIgnoreCase("sign")) {
			s.sendMessage(aq + "----- Sign Command Lines -----");
			s.sendMessage(aq + ld.first + " " + y + "This must be at the top of any ZA command sign");
			s.sendMessage(aq + ld.joingame + " " + y + "When clicked adds a player to a ZA game");
			s.sendMessage(aq + ld.areastring + " " + y + "When clicked unlocks a ZA area");
			s.sendMessage(aq + ld.perkstring + " " + y + "When clicked gives a perk");
			s.sendMessage(aq + ld.enchstring + " " + y + "When clicked enchants an item in your hand");
			s.sendMessage(aq + ld.weaponstring + " " + y + "When clicked gives a weapon");
			return;
		}
		s.sendMessage(g + a + b + " info - " + r + y + "Shows info about Ablockalypse");
		s.sendMessage(g + a + b + " join - " + r + y + "Join a game of Zombie Ablockalypse");
		s.sendMessage(g + a + b + " quit - " + r + y + "Quits a game of Zombie Ablockalypse");
		s.sendMessage(g + a + b + " setup - " + r + y + "Begin the setup of a new arena");
		s.sendMessage(g + a + b + " list - " + r + y + "List all available arenas");
		s.sendMessage(g + a + b + " help - " + r + y + "Shows a list of commands for Ablockalypse");
		s.sendMessage(g + a + b + " help sign - " + r + y + "Shows the line command lines for a sign");
	}
}
