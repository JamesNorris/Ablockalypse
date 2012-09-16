package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;
import com.github.Ablockalypse.JamesNorris.Util.External;

public class PlayerInteractEntity implements Listener {
	private ConfigurationData cd;

	/*
	 * The event called when a player hits another entity.
	 * 
	 * Used for picking a player up out of last stand.
	 */
	@EventHandler public void PIEE(PlayerInteractEntityEvent event) {
		if (cd == null)
			cd = External.ym.getConfigurationData();
		Player p = event.getPlayer();
		Entity e = event.getRightClicked();
		if (Data.players.containsKey(p) && Data.players.containsKey(e)) {
			ZAPlayer zap = Data.players.get(e);
			ZAPlayer zap2 = Data.players.get(p);
			if (zap.isInLastStand()) {
				zap.toggleLastStand();
				zap2.addPoints(cd.helppoints);
			}
		}
	}
}
