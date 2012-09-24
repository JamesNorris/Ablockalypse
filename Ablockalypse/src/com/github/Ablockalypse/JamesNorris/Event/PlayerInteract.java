package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.GameMysteryChest;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayerBase;
import com.github.Ablockalypse.JamesNorris.Implementation.GameWallSign;
import com.github.Ablockalypse.JamesNorris.Manager.YamlManager;
import com.github.Ablockalypse.JamesNorris.Threading.TeleportThread;
import com.github.Ablockalypse.JamesNorris.Util.External;

public class PlayerInteract implements Listener {
	private ConfigurationData cd;
	private YamlManager ym;

	public PlayerInteract() {
		ym = External.getYamlManager();
		cd = ym.getConfigurationData();
	}

	/*
	 * The event called when a player clicks a block.
	 * 
	 * USED:
	 * *When a ZAPlayer clicks a sign, to check the lines for strings that trigger a response.
	 */
	@EventHandler public void PIE(PlayerInteractEvent event) {
		Block b = event.getClickedBlock();
		Player p = event.getPlayer();
		if (b != null) {
			if (b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
				Sign s = (Sign) b.getState();
				GameWallSign zas;
				if (!(s instanceof GameWallSign)) {
					zas = new GameWallSign(s, ym);
				} else {
					zas = (GameWallSign) s;
				}
				zas.runLines(p);
				return;
			} else if (Data.players.containsKey(p)) {
				ZAPlayerBase zap = Data.players.get(p);
				if (b.getType() == Material.ENDER_PORTAL_FRAME) {
					new TeleportThread(zap, 5, true);
					return;
				} else if (b instanceof Chest && zap.getPoints() >= cd.mccost) {
					Chest c = (Chest) b;
					GameMysteryChest mb = new GameMysteryChest(c);
					mb.randomize();
					zap.subtractPoints(cd.mccost);
					return;
				}
			}
		}
	}
}
