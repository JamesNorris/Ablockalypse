package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.JamesNorris.Data.GlobalData;

public class PlayerTeleport implements Listener {
	/*
	 * Called when a player teleports from one location to the other.
	 * Used mainly for preventing teleportation for ZAPlayers when an ender pearl is thrown.
	 */
	@EventHandler public void PTE(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.ENDER_PEARL && GlobalData.players.containsKey(event.getPlayer()))
			event.setCancelled(true);
	}
}
