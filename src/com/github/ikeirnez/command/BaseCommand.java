package com.github.ikeirnez.command;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.ikeirnez.util.CommandUtil;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.External;
import com.github.jamesnorris.event.GameCreateEvent;
import com.github.jamesnorris.event.GamePlayerLeaveEvent;
import com.github.jamesnorris.event.bukkit.PlayerInteract;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.storage.MapDataStorage;

public class BaseCommand extends CommandUtil implements CommandExecutor {
    private DataContainer data = DataContainer.data;

    @Override public boolean onCommand(CommandSender sender, Command cmd, String inf, String[] args) {
        if (cmd.getName().equalsIgnoreCase("za")) {
            if (!sender.hasPermission("za.base")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use Ablockalypse commands!");
                return true;
            }

            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                showHelp(sender, args, cmd.getLabel());
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                list(sender);
                return true;
            } else if (args[0].equalsIgnoreCase("join")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(notPlayer);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                if (!sender.hasPermission("za.join")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to join games!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                Player player = (Player) sender;
                ZAPlayer zap = data.getZAPlayer(player, gameName, true);
                zap.loadPlayerToGame(gameName);
                return true;
            } else if (args[0].equalsIgnoreCase("quit")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(notPlayer);
                    return true;
                }
                Player player = (Player) sender;
                if (!data.players.containsKey(player)) {
                    sender.sendMessage(ChatColor.RED + "You must be in a game to do that!");
                    return true;
                }
                ZAPlayer zap = data.players.get(player);
                Game zag = zap.getGame();
                GamePlayerLeaveEvent GPLE = new GamePlayerLeaveEvent(zap, zag);
                Bukkit.getPluginManager().callEvent(GPLE);
                if (!GPLE.isCancelled()) {
                    sender.sendMessage(ChatColor.AQUA + "Successfully quit the Ablockalypse game: " + ChatColor.GOLD + zag.getName());
                    zag.removePlayer(player);
                    return true;
                }
                return true;
            } else if (args[0].equalsIgnoreCase("create")) {
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "That game already exists!");
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                Game zag = new Game(gameName);
                GameCreateEvent gce = new GameCreateEvent(zag, sender, null);
                Bukkit.getServer().getPluginManager().callEvent(gce);
                if (!gce.isCancelled()) {
                    sender.sendMessage(ChatColor.GRAY + "You have created a new ZA game called " + gameName);
                } else {
                    zag.remove();
                }
                return true;
            } else if (args[0].equalsIgnoreCase("barrier")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(notPlayer);
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                Player player = (Player) sender;
                PlayerInteract.barrierPlayers.put(player.getName(), data.getGame(gameName, true));
                player.sendMessage(ChatColor.GRAY + "Click the center of a 3x3 section of fence to make a barrier.");
                return true;
            } else if (args[0].equalsIgnoreCase("mainframe")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(notPlayer);
                    return true;
                }
                Player p = (Player) sender;
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                Game zag = data.getGame(gameName, false);
                zag.setMainframe(new Mainframe(zag, p.getLocation()));
                sender.sendMessage(ChatColor.GRAY + "You have set the mainframe for " + gameName);
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length == 2) {
                    String gameName = args[1];
                    if (!data.gameExists(gameName)) {
                        sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                        return true;
                    }
                    // TODO "are you sure?" command
                    Game zag = data.getGame(gameName, true);
                    zag.remove();
                    sender.sendMessage(ChatColor.GRAY + "You have removed the game " + gameName);
                    return true;
                } else if (args.length == 1) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "You must be a player to remove a game object!");
                        return true;
                    }
                    Player p = (Player) sender;
                    sender.sendMessage(ChatColor.GRAY + "Click a ZA object to remove it.");
                    PlayerInteract.removers.add(p.getName());
                }
            } else if (args[0].equalsIgnoreCase("spawner")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(notPlayer);
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                Player player = (Player) sender;
                PlayerInteract.spawnerPlayers.put(player.getName(), data.getGame(gameName, true));
                sender.sendMessage(ChatColor.GRAY + "Click a block to create a spawner.");
                return true;
            } else if (args[0].equalsIgnoreCase("area")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(notPlayer);
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                Player player = (Player) sender;
                PlayerInteract.areaPlayers.put(player.getName(), data.getGame(gameName, true));
                sender.sendMessage(ChatColor.GRAY + "Click a block to select point 1.");
                return true;
            } else if (args[0].equalsIgnoreCase("chest")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(notPlayer);
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                Player player = (Player) sender;
                PlayerInteract.chestPlayers.put(player.getName(), data.getGame(gameName, true));
                sender.sendMessage(ChatColor.GRAY + "Click a chest to turn it into a mystery chest.");
                return true;
            } else if (args[0].equalsIgnoreCase("mapdata")) {
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! /za mapdata <game> load - is correct!");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to save/load mapdata! (A location on the map is needed)");
                    return true;
                }
                Player player = (Player) sender;
                String gameName = args[1];
                if (args[2].equalsIgnoreCase("load")) {
                    String oldFile = gameName + "_mapdata.bin";
                    File saveFile = new File(Ablockalypse.instance.getDataFolder(), File.separatorChar + "map_data" + File.separatorChar + oldFile);
                    try {
                        MapDataStorage mds = (MapDataStorage) External.load(saveFile.getPath());
                        if (!mds.possibleKey(player.getLocation())) {
                            sender.sendMessage(ChatColor.RED + "You are not in the correct location to load mapdata! \nPlease make sure you are in the same spot where the mapdata was saved.");
                        }
                        mds.loadToGame(player.getLocation());
                        sender.sendMessage(ChatColor.GRAY + "Map data loaded! \nPlease note that this is a single event, and is not constantly updated/loaded.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (args[2].equalsIgnoreCase("save")) {
                    String newFile = gameName + "_mapdata.bin";
                    try {
                        File saveFile = new File(Ablockalypse.instance.getDataFolder(), File.separatorChar + External.mapdatafolderlocation + newFile);
                        if (!saveFile.exists())
                            saveFile.createNewFile();
                        External.save(new MapDataStorage(player.getLocation(), gameName), External.filelocation + External.mapdatafolderlocation + newFile);
                        sender.sendMessage(ChatColor.GRAY + "Map data snapshot saved! \nPlease note that this data is only a snapshot, and never updates automatically.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "You must provide the final argument 'load/save'!");
                    return true;
                }
                return true;
            }
            return true;
        }
        return true;
    }
}
