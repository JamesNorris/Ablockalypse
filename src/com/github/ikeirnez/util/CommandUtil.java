package com.github.ikeirnez.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.ManagementHelpTopic;

public class CommandUtil {
    public static String invalidSetting = ChatColor.RED + "That setting does not exist!";
    public static String joinGame = ChatColor.AQUA + "You have joined a game of Zombie Ablockalypse";
    public static String noMaintainPerms = ChatColor.RED + "You don't have permission to perform maintenance.";
    public static String notPlayer = ChatColor.RED + "You must be a player to use that command.";
    public static String settingChanged = ChatColor.GRAY + "That setting has been changed.";

    /**
     * Separates a collection into a comma separated list
     * 
     * @param inputArray An object to implode
     * @param glueString The string to be appended before each input array value after the first value
     * @param finalGlueString The string to be appended at the very end of the input array, before the last value
     * @return The finalized string
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
        Ablockalypse.getData();
        s.sendMessage(ChatColor.GOLD + "Available Games: " + ChatColor.YELLOW + implode(DataContainer.data.games.keySet().toArray(), ChatColor.GOLD + ", " + ChatColor.YELLOW, ChatColor.GOLD + " and " + ChatColor.YELLOW));
    }

    // Note: Max lines per view is 10
    /**
     * Help, Setup, List, Info, Join, and Quit all displayed to the CommandSender.
     * 
     * @param s CommandSender
     * @param args The argument array of the command sent
     * @param alias The string input for the command
     */
    public void showHelp(CommandSender s, String[] args, String alias) {
        String aliasView = "/" + alias;
        StringBuilder sb = new StringBuilder();
        ChatColor reset = ChatColor.RESET;
        ChatColor gold = ChatColor.GOLD;
        ChatColor bold = ChatColor.BOLD;
        ChatColor gray = ChatColor.GRAY;
        ChatColor red = ChatColor.RED;
        boolean sent = false;
        for (ManagementHelpTopic topic : ManagementHelpTopic.values()) {
            if (topic.matches(s, args, aliasView)) {
                topic.showHelp(s, args, aliasView);
                sent = true;
            }
        }
        if (!sent) {
            sb.append(red + "----- Ablockalypse Help -----");
            sb.append(gold + aliasView + bold + " list - " + reset + gray + "List all available arenas");
            sb.append(gold + aliasView + bold + " join <game> - " + reset + gray + "Join a game of Zombie Ablockalypse");
            sb.append(gold + aliasView + bold + " quit - " + reset + gray + "Quits a game of Zombie Ablockalypse");
            if (s.hasPermission("za.create")) {
                for (ManagementHelpTopic topic : ManagementHelpTopic.values()) {
                    sb.append(gold + aliasView + bold + " help " + topic.getName() + " - " + reset + gray + topic.getDescription());
                }
            }
            s.sendMessage(sb.toString());
        }
    }
}
