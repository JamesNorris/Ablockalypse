package com.github.iKeirNez.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.Local;
import com.github.JamesNorris.Util.MiscUtil;

public class CommandUtil extends DataManipulator {
    public static String invalidSetting = ChatColor.RED + "That setting does not exist!";
    public static String joinGame = ChatColor.AQUA + "You have joined a game of Zombie Ablockalypse";
    public static String noMaintainPerms = ChatColor.RED + "You don't have permission to perform maintenance.";
    public static String notPlayer = ChatColor.RED + "You must be a player to use that command.";
    public static String settingChanged = ChatColor.GRAY + "That setting has been changed.";

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
        MiscUtil.replyToSender(s, ChatColor.GOLD + "Available Games: " + ChatColor.YELLOW + implode(data.games.keySet().toArray(), ChatColor.GOLD + ", " + ChatColor.YELLOW, ChatColor.GOLD + " and " + ChatColor.YELLOW));
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
        StringBuilder sb = new StringBuilder();
        ChatColor res = ChatColor.RESET;
        ChatColor g = ChatColor.GOLD;
        ChatColor b = ChatColor.BOLD;
        ChatColor gr = ChatColor.GRAY;
        ChatColor r = ChatColor.RED;
        if (args.length == 2 && args[1].equalsIgnoreCase("sign") && s.hasPermission("za.create")) {
            sb.append(r + "----- Ablockalypse Sign Lines -----");
            sb.append(r + Local.BASESTRING.getSetting() + " " + gr + "This must be at the top of any ZA command sign");// TODO ix when LD is changed to new form
            sb.append(r + Local.BASEJOINSTRING.getSetting() + " " + gr + "When clicked adds a player to a ZA game");
            sb.append(r + Local.BASEAREASTRING.getSetting() + " " + gr + "When clicked unlocks a ZA area");
            sb.append(r + Local.BASEPERKSTRING.getSetting() + " " + gr + "When clicked gives a perk");
            sb.append(r + Local.BASEENCHANTMENTSTRING.getSetting() + " " + gr + "When clicked enchants an item in your hand");
            sb.append(r + Local.BASEWEAPONSTRING.getSetting() + " " + gr + "When clicked gives a weapon");
            return;
        } else if (args.length == 2 && args[1].equalsIgnoreCase("setup") && s.hasPermission("za.create")) {
            sb.append(r + "----- Ablockalypse Setup Help -----");
            sb.append(g + a + b + " create <game> - " + res + gr + "Creates a new game");
            sb.append(g + a + b + " barrier <game> - " + res + gr + "Creates a barrier for the game");
            sb.append(g + a + b + " area <game> - " + res + gr + "Creates an area for the game");
            sb.append(g + a + b + " spawner <game> - " + res + gr + "Creates a spawner for the game");
            sb.append(g + a + b + " mainframe <game> - " + res + gr + "Sets the mainframe for the game");
            sb.append(g + a + b + " chest <game> - " + res + gr + "Adds a mystery chest to the game");
            sb.append(g + a + b + " remove <game> - " + res + gr + "Removes an entire game");
            sb.append(g + a + b + " remove - " + res + gr + "Removes a barrier, area, or spawner");
        } else if (args.length == 2 && args[1].equalsIgnoreCase("settings") && s.hasPermission("za.create")) {
            sb.append(r + "----- Ablockalypse Settings Help -----");
            sb.append(g + a + b + " settings <game> - " + res + gr + "Shows the settings of a game");
            sb.append(g + a + b + " settings <game> <setting> <boolean> - " + res + gr + "Changes a setting of a game");
            sb.append(g + "" + b + "Settings: " + res + g + " FF (Friendly Fire)");
        } else if (args.length == 2 && args[1].equalsIgnoreCase("mapdata") && s.hasPermission("za.create")) {
            //@formatter:off
            sb.append(gr + "Mapdata is an innovative new way to deliver maps from one server to another. " +
            		"\nWhen Ablockalypse was first created so that map packs would be released, but we ran into a problem. " +
            		"\nThe problem was that physical game objects (barriers, etc) could not be moved to other games. " +
            		"\nMapdata is a solution to that, and loads physical game objects to the new server/game." +
            		res + "\n---------------------------------------------------------------------------------------");
            //@formatter:on
            sb.append(g + a + b + " mapdata <game> save - " + res + gr + "Saves a mapdata file for the game given");
            sb.append(g + a + b + " mapdata <game> load - " + res + gr + "Loads a mapdata file to a game map");
        } else {
            sb.append(r + "----- Ablockalypse Help -----");
            sb.append(g + a + b + " info - " + res + gr + "Shows info about Ablockalypse");
            sb.append(g + a + b + " list - " + res + gr + "List all available arenas");
            sb.append(g + a + b + " join <game> - " + res + gr + "Join a game of Zombie Ablockalypse");
            sb.append(g + a + b + " quit - " + res + gr + "Quits a game of Zombie Ablockalypse");
            if (s.hasPermission("za.create")) {
                sb.append(g + a + b + " help sign - " + res + gr + "Shows sign commandline requirements");
                sb.append(g + a + b + " help setup - " + res + gr + "Shows how to setup games");
                sb.append(g + a + b + " help settings - " + res + gr + "Shows changeable game settings");
                sb.append(g + a + b + " help mapdata - " + res + gr + "Shows advanced map creation help");
            }
        }
        MiscUtil.replyToSender(s, sb.toString());
    }
}
