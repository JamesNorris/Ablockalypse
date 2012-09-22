package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.MysteryChest;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;
import com.github.Ablockalypse.JamesNorris.Implementation.ZASign;
import com.github.Ablockalypse.JamesNorris.Manager.YamlManager;
import com.github.Ablockalypse.JamesNorris.Threading.TeleportThread;
import com.github.Ablockalypse.JamesNorris.Util.External;

public class PlayerInteract implements Listener {
	private YamlManager ym;
	private ConfigurationData cd;

	/*
	 * The event called when a player clicks a block.
	 * 
	 * USED:
	 * *When a ZAPlayer clicks a sign, to check the lines for strings that trigger a response.
	 */
	public void PIE(final PlayerInteractEvent event) {
		if (ym == null || cd == null) {
			ym = External.ym;
			cd = ym.getConfigurationData();
		}
		final Block b = event.getClickedBlock();
		final Player p = event.getPlayer();
		if (b instanceof Sign) {
			final Sign s = (Sign) b;
			ZASign zas;
			if (!(s instanceof ZASign)) {
				zas = new ZASign(s, ym);
			} else {
				zas = (ZASign) s;
			}
			zas.runLines(p);
			if (cd.effects)
				s.getWorld().playEffect(s.getLocation(), Effect.POTION_BREAK, 1);
			return;
		} else if (Data.players.containsKey(p)) {
			final ZAPlayer zap = Data.players.get(p);
			if (b.getType() == Material.ENDER_PORTAL_FRAME) {
				new TeleportThread(zap, 5, true);
				return;
			} else if (b instanceof Chest && zap.getPoints() >= cd.mccost) {
				final Chest c = (Chest) b;
				final MysteryChest mb = new MysteryChest(c);
				mb.randomize();
				zap.subtractPoints(cd.mccost);
				return;
			}
		}
	}
}
