package com.github.Ablockalypse.JamesNorris;

import org.bukkit.plugin.PluginManager;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Event.BlockPlace;
import com.github.Ablockalypse.JamesNorris.Event.EntityBreakDoor;
import com.github.Ablockalypse.JamesNorris.Event.EntityDamage;
import com.github.Ablockalypse.JamesNorris.Event.EntityDeath;
import com.github.Ablockalypse.JamesNorris.Event.EntityExplode;
import com.github.Ablockalypse.JamesNorris.Event.EntityTarget;
import com.github.Ablockalypse.JamesNorris.Event.PlayerDeath;
import com.github.Ablockalypse.JamesNorris.Event.PlayerInteract;
import com.github.Ablockalypse.JamesNorris.Event.PlayerInteractEntity;
import com.github.Ablockalypse.JamesNorris.Event.PlayerMove;
import com.github.Ablockalypse.JamesNorris.Event.PlayerPickupItem;
import com.github.Ablockalypse.JamesNorris.Event.PlayerQuit;
import com.github.Ablockalypse.JamesNorris.Event.PlayerRespawn;
import com.github.Ablockalypse.JamesNorris.Event.PlayerTeleport;
import com.github.Ablockalypse.JamesNorris.Event.PlayerToggleSneak;
import com.github.Ablockalypse.JamesNorris.Event.ProjectileHit;
import com.github.Ablockalypse.iKeirNez.Command.BaseCommand;

public class RegistrationManager {
	/**
	 * Loads all events to the plugin.
	 * 
	 * @param instance The instance of the Ablockalypse plugin
	 */
	public static void register(final Ablockalypse instance) {
		final PluginManager pm = instance.getServer().getPluginManager();
		/* EVENTS */
		pm.registerEvents(new EntityDamage(), instance);
		pm.registerEvents(new PlayerDeath(), instance);
		pm.registerEvents(new PlayerInteract(), instance);
		pm.registerEvents(new PlayerInteractEntity(), instance);
		pm.registerEvents(new BlockPlace(), instance);
		pm.registerEvents(new EntityDeath(), instance);
		pm.registerEvents(new PlayerPickupItem(), instance);
		pm.registerEvents(new ProjectileHit(), instance);
		pm.registerEvents(new PlayerMove(), instance);
		pm.registerEvents(new PlayerToggleSneak(), instance);
		pm.registerEvents(new EntityBreakDoor(), instance);
		pm.registerEvents(new EntityExplode(), instance);
		pm.registerEvents(new PlayerTeleport(), instance);
		pm.registerEvents(new PlayerQuit(), instance);
		pm.registerEvents(new EntityTarget(), instance);
		pm.registerEvents(new PlayerRespawn(), instance);
		/* COMMANDS */
		instance.getCommand("za").setExecutor(new BaseCommand());
	}
}
