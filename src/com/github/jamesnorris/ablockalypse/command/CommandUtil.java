package com.github.jamesnorris.ablockalypse.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.enumerated.Local;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;

public class CommandUtil {
    public static String invalidSetting = ChatColor.RED + "That setting does not exist!";// TODO localization in the local.yml with the &g color codes
    public static String joinGame = ChatColor.AQUA + "You have joined a game of Zombie Ablockalypse";
    public static String noMaintainPerms = ChatColor.RED + "You don't have permission to perform maintenance.";
    public static String requiresPlayer = ChatColor.RED + "You must be a player to use that command.";
    public static String settingChanged = ChatColor.GRAY + "That setting has been changed.";
    //@formatter:off
    public static final ChatMenu BASE_MENU = new ChatMenu(ChatColor.RED + "Ablockalypse Help", new String[] {
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " help - " + ChatColor.RESET + ChatColor.GRAY + "Shows this menu",
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " list - " + ChatColor.RESET + ChatColor.GRAY + "Shows list items",
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " join <game> - " + ChatColor.RESET + ChatColor.GRAY + "Joins a game",
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " quit - " + ChatColor.RESET + ChatColor.GRAY + "Quit a game",
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " game - " + ChatColor.RESET + ChatColor.GRAY + "Shows the game menu",
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " object - " + ChatColor.RESET + ChatColor.GRAY + "Shows the object menu"
    });
    public static final ChatMenu LIST_MENU = new ChatMenu(ChatColor.RED + "Available Lists", new String[] {
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " list games - " + ChatColor.RESET + ChatColor.GRAY + "List all available games",
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " list signs - " + ChatColor.RESET + ChatColor.GRAY + "List all available sign lines",
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " list objects - " + ChatColor.RESET + ChatColor.GRAY + "List all available game objects"
    });
    public static final ChatMenu GAME_MENU = new ChatMenu(ChatColor.RED + "Ablockalypse Game Options", new String[] {
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " game create <game> - " + ChatColor.RESET + ChatColor.GRAY + "Creates a new game",
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " game remove <game> - " + ChatColor.RESET + ChatColor.GRAY + "Creates a new game object",
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " game mapdata <game> <save/load> - " + ChatColor.RESET + ChatColor.GRAY + "Saves/loads mapdata",
    });
    public static final ChatMenu OBJECT_MENU = new ChatMenu(ChatColor.RED + "Ablockalypse Object Options", new String[] {
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " object create <object> <game> - " + ChatColor.RESET + ChatColor.GRAY + "Creates a new game object",
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " object remove - " + ChatColor.RESET + ChatColor.GRAY + "Removes a game object",
            ChatColor.GOLD + "/za" + ChatColor.BOLD + " object power <game> <true/false> - " + ChatColor.RESET + ChatColor.GRAY + "Enables/disables power for a game object"
    });
    public static final ChatMenu GAME_LIST = new ChatMenu(ChatColor.RED + "Games " + ChatColor.RESET + "(" + ChatColor.GREEN + "#" + ChatColor.RESET + " = open, " + ChatColor.RED + "#" + ChatColor.RESET + " = full)", new String[] {
    		ChatColor.RED + "NOT LOADED"
    });
    public static final ChatMenu SIGN_LIST = new ChatMenu(ChatColor.RED + "Sign Types ((#) = sign line #)", new String[] {
            ChatColor.GOLD + "" + Local.BASE_STRING.getSetting() + ChatColor.GRAY + " - (1) Required by all ZA signs", 
            ChatColor.GOLD + "" + Local.BASE_JOIN_STRING.getSetting() + ChatColor.GRAY + " - (2) Joins a player to a game", 
            ChatColor.GOLD + "" + Local.BASE_PERK_STRING.getSetting() + ChatColor.GRAY + " - (2) Gives a perk", 
            ChatColor.GOLD + "" + Local.BASE_ENCHANTMENT_STRING.getSetting() + ChatColor.GRAY + " - (2) Gives an enchantment/pack-a-punch", 
            ChatColor.GOLD + "" + Local.BASE_WEAPON_STRING.getSetting() + ChatColor.GRAY + " - (2) Gives a weapon", 
            ChatColor.GOLD + "" + Local.BASE_PASSAGE_STRING.getSetting() + ChatColor.GRAY + " - (2) Opens the nearest passage"
    });
    public static final ChatMenu OBJECT_LIST = new ChatMenu(ChatColor.RED + "Object Types", new String[] {
            ChatColor.GOLD + "Barrier" + ChatColor.GRAY + " - A 3x3 wall that is broken by mobs.",
            ChatColor.GOLD + "Teleporter" + ChatColor.GRAY + " - Sends the player to the mainframe.",
            ChatColor.GOLD + "Mainframe" + ChatColor.GRAY + " - The teleporter that all lead to.",
            ChatColor.GOLD + "MobSpawner" + ChatColor.GRAY + " - A block where game mobs spawn.",
            ChatColor.GOLD + "Passage" + ChatColor.GRAY + " - A wall that reveals new areas of the map.",
            ChatColor.GOLD + "MysteryBox" + ChatColor.GRAY + " - A chest that gives random items at a cost."
    });
    //@formatter:on
    protected void updateGameListMenu() {
        List<String> gameNames = new ArrayList<String>();
        for (Game game : Ablockalypse.getData().getObjectsOfType(Game.class)) {
            gameNames.add((game.getPlayers().size() < (Integer) Setting.MAX_PLAYERS.getSetting() ? ChatColor.GREEN : ChatColor.RED) + game.getName());
        }
        if (gameNames.isEmpty()) {
            gameNames.add(ChatColor.GRAY + "No games to display.");
        }
        GAME_LIST.setMenuItems(gameNames.toArray(new String[gameNames.size()]));
    }
}
