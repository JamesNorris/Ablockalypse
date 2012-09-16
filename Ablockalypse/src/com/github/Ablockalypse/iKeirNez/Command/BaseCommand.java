package com.github.Ablockalypse.iKeirNez.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGame;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;
import com.github.Ablockalypse.iKeirNez.Util.CommandUtil;
import com.github.Ablockalypse.iKeirNez.Util.CommonMsg;
import com.github.Ablockalypse.iKeirNez.Util.StringFunctions;

// TODO can you use local.yml and LocalizationData.java to change all strings?
public class BaseCommand extends CommandUtil implements CommandExecutor {
	@Override public boolean onCommand(CommandSender sender, Command cmd, String inf, String[] args) {
		if (cmd.getName().equalsIgnoreCase("za")) {
			String alias = cmd.getLabel();
			if (args.length == 0 || args[0].equalsIgnoreCase("help") || (args.length == 2 && args[1].equalsIgnoreCase("sign"))) {
				showHelp(sender, args, alias);
			} else if (args[0].equalsIgnoreCase("list")) {
				list(sender);
			} else if (args[0].equalsIgnoreCase("join")) {
				if (args.length == 2 && sender.hasPermission("za.join")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						String gameName = args[1];
						ZAPlayer zap;
						if (Data.players.containsKey(player)) {
							zap = Data.players.get(player);
						} else {
							if (Data.games.containsKey(gameName)) {
								ZAGame game = Data.games.get(gameName);
								zap = new ZAPlayer(player, game);
								zap.loadPlayerToGame(gameName);
								sender.sendMessage(CommonMsg.joinGame);
							} else {
								sender.sendMessage("That game does not exist.");
								return true;
							}
							return true;
						}
					} else {
						sender.sendMessage(CommonMsg.notPlayer);
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have permission to join games");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("info")) {
				sender.sendMessage(ChatColor.GOLD + "Zombie Ablockalypse version " + Data.version);
				sender.sendMessage(ChatColor.GOLD + "Developed by " + StringFunctions.implode(Data.authors.toArray(), ", ", " and "));
				return true;
			} else if (args[0].equalsIgnoreCase("quit")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (Data.players.containsKey(player)) {
						ZAPlayer zap = Data.players.get(player);
						zap.finalize();
						sender.sendMessage(ChatColor.AQUA + "Successfully quit the Zombie Ablockalypse game.");
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "You must be in a Zombie Ablockalypse game to do that.");
						return true;
					}
				} else {
					sender.sendMessage(CommonMsg.notPlayer);
					return true;
				}
			}
		}
		return true;
	}
}
