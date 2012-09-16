package com.github.Ablockalypse.iKeirNez.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.Ablockalypse.JamesNorris.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data;
import com.github.Ablockalypse.JamesNorris.Util.External;

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
		ConfigurationData cd = External.cd;
		String a = "/" + alias;
		ChatColor r = ChatColor.RESET;
		ChatColor g = ChatColor.GOLD;
		ChatColor b = ChatColor.BOLD;
		ChatColor y = ChatColor.YELLOW;
		ChatColor aq = ChatColor.AQUA;
		if (args.length == 2 && args[1].equalsIgnoreCase("sign")) {
			s.sendMessage(aq + "----- Sign Command Lines -----");
			s.sendMessage(aq + cd.first + " " + y + "This must be at the top of any ZA command sign");
			s.sendMessage(aq + cd.joingame + " " + y + "A sign with this line, when clicked adds a player to a ZA game");
			s.sendMessage(aq + cd.areastring + " " + y + "A sign with this line, when clicked unlocks a ZA area");
			s.sendMessage(aq + cd.perkstring + " " + y + "A sign with this line, when clicked gives a perk");
			s.sendMessage(aq + cd.enchstring + " " + y + "A sign with this line, when clicked enchants an item in your hand");
			s.sendMessage(aq + cd.weaponstring + " " + y + "A sign with this line, when clicked gives a weapon");
			return;
		}
		s.sendMessage(g + a + b + " help sign - " + r + y + "Shows the line command lines for a sign");
		s.sendMessage(g + a + b + " join - " + r + y + "Join a game of Zombie Ablockalypse");
		s.sendMessage(g + a + b + " quit - " + r + y + "Quits a game of Zombie Ablockalypse");
		s.sendMessage(g + a + b + " setup - " + r + y + "Begin the setup of a new Zombie Ablockalypse Arena");
		s.sendMessage(g + a + b + " list - " + r + y + "List all available arenas");
		s.sendMessage(g + a + b + " help - " + r + y + "Shows a list of commands you can use with Zombie Ablockalypse");
	}
}
