package com.github.iKeirNez.Command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Event.GameCreateEvent;
import com.github.JamesNorris.Event.GamePlayerLeaveEvent;
import com.github.JamesNorris.Event.Bukkit.PlayerInteract;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.iKeirNez.Util.CommandUtil;

public class BaseCommand extends CommandUtil implements CommandExecutor {
	private LocalizationData ld;

	@Override public boolean onCommand(CommandSender sender, Command cmd, String inf, String[] args) {
		if (cmd.getName().equalsIgnoreCase("za")) {
			if (ld == null)
				ld = External.ym.getLocalizationData();
			String alias = cmd.getLabel();
			if (args.length == 0 || args[0].equalsIgnoreCase("help") || (args.length == 2 && args[1].equalsIgnoreCase("sign"))) {
				showHelp(sender, args, alias);
				return true;
			} else if (args[0].equalsIgnoreCase("list")) {
				list(sender);
				return true;
			} else if (args[0].equalsIgnoreCase("join")) {
				if (args.length == 2) {
					if (sender.hasPermission("za.join")) {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							String gameName = args[1];
							if (GlobalData.gameExists(gameName)) {
								ZAPlayer zap = GlobalData.findZAPlayer(player, gameName);
								zap.loadPlayerToGame(gameName);
								return true;
							} else {
								sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
								return true;
							}
						} else {
							sender.sendMessage(CommandUtil.notPlayer);
							return true;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You do not have permission to join games!");
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("info")) {
				sender.sendMessage(ChatColor.GOLD + "Zombie Ablockalypse version: " + ChatColor.RED + GlobalData.version);
				sender.sendMessage(ChatColor.GOLD + "Developed by: " + ChatColor.RED + CommandUtil.implode(GlobalData.authors.toArray(), ", ", " and "));
				return true;
			} else if (args[0].equalsIgnoreCase("quit")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (GlobalData.players.containsKey(player)) {
						ZAPlayerBase zap = GlobalData.players.get(player);
						ZAGame zag = zap.getGame();
						GamePlayerLeaveEvent GPLE = new GamePlayerLeaveEvent(zap, zag);
						Bukkit.getPluginManager().callEvent(GPLE);
						if (!GPLE.isCancelled()) {
							sender.sendMessage(ChatColor.AQUA + "Successfully quit the Ablockalypse game: " + ChatColor.GOLD + zag.getName());
							zag.removePlayer(player);
							return true;
						}
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "You must be in a game to do that!");
						return true;
					}
				} else {
					sender.sendMessage(CommandUtil.notPlayer);
					return true;
				}
			} else if (args[0].equalsIgnoreCase("create")) {
				if (args.length == 2) {
					String gameName = args[1];
					if (!GlobalData.gameExists(gameName)) {
						if (!sender.hasPermission("za.create")) {
							sender.sendMessage(CommandUtil.noMaintainPerms);
							return true;
						} else {
							ZAGame zag = new ZAGameBase(gameName, External.getYamlManager().getConfigurationData());
							GameCreateEvent gce = new GameCreateEvent(zag, sender, null);
							Bukkit.getServer().getPluginManager().callEvent(gce);
							if (!gce.isCancelled())
								sender.sendMessage(ChatColor.GRAY + "You have created a new ZA game called " + gameName);
							else
								zag.remove();
							return true;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "That game already exists!");
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("barrier")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("za.create")) {
						if (args.length == 2) {
							String gameName = args[1];
							Player player = (Player) sender;
							PlayerInteract.barrierPlayers.put(player.getName(), (ZAGameBase) GlobalData.findGame(gameName));
							player.sendMessage(ChatColor.GRAY + "Click the center of a 3x3 section of fence to make a barrier.");
							return true;
						} else {
							sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
							return true;
						}
					} else {
						sender.sendMessage(CommandUtil.noMaintainPerms);
						return true;
					}
				} else {
					sender.sendMessage(CommandUtil.notPlayer);
					return true;
				}
			} else if (args[0].equalsIgnoreCase("mainframe")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (args.length == 2) {
						String gameName = args[1];
						if (GlobalData.gameExists(gameName)) {
							if (sender.hasPermission("za.create")) {
								ZAGame zag = GlobalData.findGame(gameName);
								zag.setMainframe(p.getLocation());
								sender.sendMessage(ChatColor.GRAY + "You have set the mainframe for " + gameName);
								return true;
							} else {
								sender.sendMessage(CommandUtil.noMaintainPerms);
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.RED + "That game does not exist!");
							return true;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
						return true;
					}
				} else {
					sender.sendMessage(CommandUtil.notPlayer);
					return true;
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (sender.hasPermission("za.create")) {
					if (args.length == 2) {
						String gameName = args[1];
						if (GlobalData.gameExists(gameName)) {
							ZAGame zag = GlobalData.findGame(gameName);
							zag.remove();
							sender.sendMessage(ChatColor.GRAY + "You have removed the game " + gameName);
							return true;
						} else {
							sender.sendMessage(ChatColor.RED + "That game does not exist!");
							return true;
						}
					} else if (args.length == 1) {
						Player p = (Player) sender;
						p.sendMessage(ChatColor.GRAY + "Click a ZA object to remove it.");
						PlayerInteract.removers.add(p.getName());
					}
				} else {
					sender.sendMessage(CommandUtil.noMaintainPerms);
					return true;
				}
			} else if (args[0].equalsIgnoreCase("spawner")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("za.create")) {
						if (args.length == 2) {
							String gameName = args[1];
							Player player = (Player) sender;
							PlayerInteract.spawnerPlayers.put(player.getName(), (ZAGameBase) GlobalData.findGame(gameName));
							player.sendMessage(ChatColor.GRAY + "Click a block to create a spawner.");
							return true;
						} else {
							sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
							return true;
						}
					} else {
						sender.sendMessage(CommandUtil.noMaintainPerms);
						return true;
					}
				} else {
					sender.sendMessage(CommandUtil.notPlayer);
					return true;
				}
			} else if (args[0].equalsIgnoreCase("area")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("za.create")) {
						if (args.length == 2) {
							String gameName = args[1];
							Player player = (Player) sender;
							PlayerInteract.areaPlayers.put(player.getName(), (ZAGameBase) GlobalData.findGame(gameName));
							player.sendMessage(ChatColor.GRAY + "Click a block to select point 1.");
							return true;
						} else {
							sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
							return true;
						}
					} else {
						sender.sendMessage(CommandUtil.noMaintainPerms);
						return true;
					}
				} else {
					sender.sendMessage(CommandUtil.notPlayer);
					return true;
				}
			} else if (args[0].equalsIgnoreCase("chest"))
				if (sender instanceof Player) {
					if (sender.hasPermission("za.create")) {
						if (args.length == 2) {
							String gameName = args[1];
							Player player = (Player) sender;
							PlayerInteract.chestPlayers.put(player.getName(), (ZAGameBase) GlobalData.findGame(gameName));
							player.sendMessage(ChatColor.GRAY + "Click a chest to turn it into a mystery chest.");
							return true;
						} else {
							sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
							return true;
						}
					} else {
						sender.sendMessage(CommandUtil.noMaintainPerms);
						return true;
					}
				} else {
					sender.sendMessage(CommandUtil.notPlayer);
					return true;
				}
			return true;
		}
		return true;
	}
}
