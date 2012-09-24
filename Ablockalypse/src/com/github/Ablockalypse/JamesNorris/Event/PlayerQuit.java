package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGameBase;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayerBase;

public class PlayerQuit implements Listener {
	/*
	 * Called when a player leaves the server.
	 * Used for removing a player from the ZAGame when they leave.
	 */
	@EventHandler public void PPQE(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (Data.players.containsKey(p)) {
			ZAPlayerBase zap = Data.players.get(p);
			ZAGameBase zag = zap.getGame();
			zag.removePlayer(p);
			zap.finalize();
			return;
		}
	}
}
