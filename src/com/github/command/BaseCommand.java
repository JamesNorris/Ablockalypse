package com.github.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Game;
import com.github.aspect.Mainframe;
import com.github.aspect.ZAPlayer;
import com.github.event.GameCreateEvent;
import com.github.event.GamePlayerLeaveEvent;
import com.github.event.bukkit.PlayerInteract;
import com.github.manager.PermissionManager;
import com.github.utility.MiscUtil;

public class BaseCommand extends CommandUtil implements CommandExecutor {
    private DataContainer data = Ablockalypse.getData();

    @Override public boolean onCommand(CommandSender sender, Command cmd, String inf, String[] args) {
        if (cmd.getName().equalsIgnoreCase("za")) {
            if (!sender.hasPermission(PermissionManager.BASE_COMMAND)) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use Ablockalypse commands!");
                return true;
            }
            boolean args0Digit = args != null && args.length == 1 && Character.isDigit(args[0].charAt(0));
            boolean args1Digit = args != null && args.length == 2 && Character.isDigit(args[1].charAt(0));
            boolean args2Digit = args != null && args.length == 3 && Character.isDigit(args[2].charAt(0));
            boolean helpCommand = args != null && (args.length == 1 || args.length == 2 && args1Digit) && args[0].equalsIgnoreCase("help");
            if (args == null || args.length == 0 || helpCommand || args0Digit) {
                BASE_MENU.showPage(sender, args0Digit ? Integer.parseInt(args[0]) : helpCommand ? args1Digit ? Integer.parseInt(args[1]) : 1 : 1);
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                if ((args.length == 2 || args.length == 3 && args2Digit) && args[1].equalsIgnoreCase("games")) {
                    updateGameListMenu();
                    GAME_LIST.showPage(sender, args2Digit ? Integer.parseInt(args[2]) : 1);
                } else if ((args.length == 2 || args.length == 3 && args2Digit) && args[1].equalsIgnoreCase("signs")) {
                    SIGN_LIST.showPage(sender, args2Digit ? Integer.parseInt(args[2]) : 1);
                } else if ((args.length == 2 || args.length == 3 && args2Digit) && args[1].equalsIgnoreCase("objects")) {
                    OBJECT_LIST.showPage(sender, args2Digit ? Integer.parseInt(args[2]) : 1);
                } else {
                    LIST_MENU.showPage(sender, args1Digit ? Integer.parseInt(args[1]) : 1);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("join")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(requiresPlayer);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                if (!sender.hasPermission(PermissionManager.JOIN_GAMES)) {
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
                zap.loadPlayerToGame(gameName, true);
                return true;
            } else if (args[0].equalsIgnoreCase("quit")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(requiresPlayer);
                    return true;
                }
                Player player = (Player) sender;
                if (!data.isZAPlayer(player)) {
                    sender.sendMessage(ChatColor.RED + "You must be in a game to do that!");
                    return true;
                }
                ZAPlayer zap = data.getZAPlayer(player);
                Game zag = zap.getGame();
                GamePlayerLeaveEvent GPLE = new GamePlayerLeaveEvent(zap, zag);
                Bukkit.getPluginManager().callEvent(GPLE);
                if (!GPLE.isCancelled()) {
                    sender.sendMessage(ChatColor.AQUA + "Successfully quit the Ablockalypse game: " + ChatColor.GOLD + zag.getName());
                    zag.removePlayer(player);
                    return true;
                }
                return true;
            } else if (args[0].equalsIgnoreCase("game")) {
                if (!sender.hasPermission(PermissionManager.CREATE_GAMES)) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length == 3) {
                    String gameName = args[2];
                    if (args[1].equalsIgnoreCase("create")) {
                        if (data.gameExists(gameName)) {
                            sender.sendMessage(ChatColor.RED + "That game already exists!");
                            return true;
                        }
                        if (!sender.hasPermission(PermissionManager.CREATE_GAMES)) {
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
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (!data.gameExists(gameName)) {
                            sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                            return true;
                        }
                        Game zag = data.getGame(gameName, true);
                        zag.remove();
                        sender.sendMessage(ChatColor.GRAY + "You have removed the game " + gameName);
                        return true;
                    }
                } else if (args.length == 4 && args[1].equalsIgnoreCase("mapdata")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "You must be a player to save/load mapdata! (A location on the map is needed)");
                        return true;
                    }
                    String gameName = args[2];
                    Player player = (Player) sender;
                    if (args[3].equalsIgnoreCase("load")) {
                        PlayerInteract.mapDataLoadPlayers.put(player.getName(), gameName);
                        sender.sendMessage(ChatColor.GRAY + "Please click a block to load the mapdata at. This must be the bottom-left corner where you want the data loaded.");
                        return true;
                    } else if (args[3].equalsIgnoreCase("save")) {
                        PlayerInteract.mapDataSavePlayers.put(player.getName(), gameName);
                        sender.sendMessage(ChatColor.GRAY + "Please click a corner of the map to begin.");
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "You must provide the argument 'load/save'!");
                        return true;
                    }
                }
                GAME_MENU.showPage(sender, args1Digit ? Integer.parseInt(args[1]) : 1);
                return true;
            } else if (args[0].equalsIgnoreCase("object")) {
                if (!sender.hasPermission(PermissionManager.CREATE_GAMES)) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to use selection!");
                    return true;
                }
                Player player = (Player) sender;
                if (args.length == 4 && args[1].equalsIgnoreCase("create")) {
                    String gameName = args[3];
                    if (!data.gameExists(gameName)) {
                        sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                        return true;
                    }
                    Game game = data.getGame(gameName, false);
                    if (args[2].equalsIgnoreCase("barrier")) {
                        PlayerInteract.barrierPlayers.put(player.getName(), data.getGame(gameName, true));
                        player.sendMessage(ChatColor.GRAY + "Click the center of a 3x3 wall to make a barrier.");
                    } else if (args[2].equalsIgnoreCase("mainframe")) {
                        Location loc = MiscUtil.getHighestEmptyBlockUnder(player.getLocation()).getLocation();
                        game.setMainframe(new Mainframe(game, loc));
                        sender.sendMessage(ChatColor.GRAY + "You have set the mainframe for " + gameName);
                    } else if (args[2].equalsIgnoreCase("mobspawner")) {
                        PlayerInteract.spawnerPlayers.put(player.getName(), data.getGame(gameName, true));
                        sender.sendMessage(ChatColor.GRAY + "Click a block to create a spawner.");
                    } else if (args[2].equalsIgnoreCase("passage")) {
                        PlayerInteract.passagePlayers.put(player.getName(), data.getGame(gameName, true));
                        sender.sendMessage(ChatColor.GRAY + "Click a block to select point 1.");
                    } else if (args[2].equalsIgnoreCase("mysterychest")) {
                        PlayerInteract.chestPlayers.put(player.getName(), data.getGame(gameName, true));
                        sender.sendMessage(ChatColor.GRAY + "Click a chest to turn it into a mystery chest.");
                    } else if (args[2].equalsIgnoreCase("powerswitch")) {
                        PlayerInteract.powerSwitchClickers.put(player.getName(), data.getGame(gameName, true));
                        sender.sendMessage(ChatColor.GRAY + "Click a lever to turn it into a power switch.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "That is not a valid object! Please try \'/za list objects\'.");
                    }
                    return true;
                } else if (args.length == 2 && args[1].equalsIgnoreCase("remove")) {
                    Player p = (Player) sender;
                    sender.sendMessage(ChatColor.GRAY + "Click a ZA object to remove it.");
                    PlayerInteract.removers.add(p.getName());
                    return true;
                } else if (args.length == 4 && args[1].equalsIgnoreCase("power")) {
                    String gameName = args[2];
                    if (!args[3].equalsIgnoreCase("true") && !args[3].equalsIgnoreCase("false")) {
                        sender.sendMessage(ChatColor.RED + "Incorrect syntax! /za power <game> <true/false> - is correct!");
                        return true;
                    }
                    boolean power = Boolean.parseBoolean(args[3]);
                    PlayerInteract.powerClickers.put(player.getName(), power);
                    PlayerInteract.powerClickerGames.put(player.getName(), gameName);
                    player.sendMessage(ChatColor.GRAY + "Please click on a powerable object to " + (power ? "enable" : "disable") + " power requirement.");
                    return true;
                }
                OBJECT_MENU.showPage(sender, args1Digit ? Integer.parseInt(args[1]) : 1);
                return true;
            }
            BASE_MENU.showPage(sender, args0Digit ? Integer.parseInt(args[0]) : helpCommand ? args1Digit ? Integer.parseInt(args[1]) : 1 : 1);
            return true;
        }
        return true;
    }
}
