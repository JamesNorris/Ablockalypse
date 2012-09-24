package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Threading.RespawnThread;

public class PlayerRespawn implements Listener {
	/*
	 * Called when a player respawns.
	 * Mainly used for sending the player back to the mainframe.
	 */
	@EventHandler public void PRE(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		if (Data.players.containsKey(p)) {
			new RespawnThread(p, 5, true);
		}
	}
}
