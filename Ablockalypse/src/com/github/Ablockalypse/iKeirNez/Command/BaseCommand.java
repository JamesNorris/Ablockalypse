package com.github.Ablockalypse.iKeirNez.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Data.LocalizationData;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;
import com.github.Ablockalypse.JamesNorris.Util.External;
import com.github.Ablockalypse.iKeirNez.Util.CommandUtil;
import com.github.Ablockalypse.iKeirNez.Util.CommonMsg;
import com.github.Ablockalypse.iKeirNez.Util.StringFunctions;

// TODO can you use local.yml and LocalizationData.java to change all strings?
public class BaseCommand extends CommandUtil implements CommandExecutor {
	private LocalizationData ld;

	@Override public boolean onCommand(CommandSender sender, Command cmd, String inf, String[] args) {
		if (cmd.getName().equalsIgnoreCase("za")) {
			if (ld == null)
				ld = External.ym.getLocalizationData();
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
						if (!Data.gameExists(gameName)) {
							sender.sendMessage(ChatColor.RED + "That game was not found");
							return true;
						}
						ZAPlayer zap = Data.findZAPlayer(player, gameName);
						zap.loadPlayerToGame(gameName);
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
