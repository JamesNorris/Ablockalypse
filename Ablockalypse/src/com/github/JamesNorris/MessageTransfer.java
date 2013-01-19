package com.github.JamesNorris;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.JamesNorris.Enumerated.MessageDirection;
import com.github.JamesNorris.Event.MessageTransferEvent;
import com.github.JamesNorris.Util.SpecificMessage;
import com.github.zathrus_writer.commandsex.api.XMPPAPI;

public class MessageTransfer {
	public static void sendMessage(SpecificMessage message) {
		MessageDirection direction = message.getDirection();
		MessageTransferEvent mte = new MessageTransferEvent(message, direction);
		Bukkit.getPluginManager().callEvent(mte);
		if (!mte.isCancelled()) {
			switch (direction) {
				case CONSOLE_ERROR:
					System.err.println(ChatColor.stripColor(message.getMessage()));
					break;
				case CONSOLE_OUTPUT:
					System.out.println(ChatColor.stripColor(message.getMessage()));
				break;
				case PLAYER_BROADCAST:
					MessageTransfer.broadcast(message.getMessage(), message.getExceptions(), false);
				break;
				case PLAYER_PRIVATE:
					MessageTransfer.broadcast(message.getMessage(), message.getTargets(), true);
				break;
				case XMPP:
					XMPPAPI.sendMessage(message.getMessage());
				break;
			}
		}
	}

	protected static void broadcast(String message, List<String> attachment, boolean targetBased) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			String s = p.getName();
			if ((targetBased) ? !attachment.contains(s) : attachment.contains(s))
				Bukkit.getPlayer(s).sendMessage(message);
		}
	}
}
