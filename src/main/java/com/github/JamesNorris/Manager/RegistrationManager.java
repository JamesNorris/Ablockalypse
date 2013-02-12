package com.github.JamesNorris.Manager;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.github.Ablockalypse;
import com.github.JamesNorris.XMPP;
import com.github.JamesNorris.Event.Bukkit.BlockBreak;
import com.github.JamesNorris.Event.Bukkit.SignChange;
import com.github.JamesNorris.Event.Bukkit.EntityBreakDoor;
import com.github.JamesNorris.Event.Bukkit.EntityDamage;
import com.github.JamesNorris.Event.Bukkit.EntityDamageByEntity;
import com.github.JamesNorris.Event.Bukkit.EntityDeath;
import com.github.JamesNorris.Event.Bukkit.EntityExplode;
import com.github.JamesNorris.Event.Bukkit.EntityTarget;
import com.github.JamesNorris.Event.Bukkit.PlayerDeath;
import com.github.JamesNorris.Event.Bukkit.PlayerDropItem;
import com.github.JamesNorris.Event.Bukkit.PlayerInteract;
import com.github.JamesNorris.Event.Bukkit.PlayerInteractEntity;
import com.github.JamesNorris.Event.Bukkit.PlayerJoin;
import com.github.JamesNorris.Event.Bukkit.PlayerKick;
import com.github.JamesNorris.Event.Bukkit.PlayerMove;
import com.github.JamesNorris.Event.Bukkit.PlayerPickupItem;
import com.github.JamesNorris.Event.Bukkit.PlayerQuit;
import com.github.JamesNorris.Event.Bukkit.PlayerRespawn;
import com.github.JamesNorris.Event.Bukkit.PlayerTeleport;
import com.github.JamesNorris.Event.Bukkit.PlayerToggleSneak;
import com.github.JamesNorris.Event.Bukkit.ProjectileHit;
import com.github.iKeirNez.Command.BaseCommand;

public class RegistrationManager {
    /**
     * Loads all events to the plugin.
     * 
     * @param instance The instance of the Ablockalypse plugin
     */
    public static void register(final Plugin instance) {
        final PluginManager pm = instance.getServer().getPluginManager();
        /* EVENTS */
        pm.registerEvents(new EntityDamage(), instance);
        pm.registerEvents(new PlayerDeath(), instance);
        pm.registerEvents(new PlayerInteract(), instance);
        pm.registerEvents(new PlayerInteractEntity(), instance);
        pm.registerEvents(new SignChange(), instance);
        pm.registerEvents(new EntityDeath(), instance);
        pm.registerEvents(new PlayerPickupItem(), instance);
        pm.registerEvents(new ProjectileHit(), instance);
        pm.registerEvents(new PlayerMove(), instance);
        pm.registerEvents(new PlayerToggleSneak(), instance);
        pm.registerEvents(new EntityBreakDoor(), instance);
        pm.registerEvents(new PlayerTeleport(), instance);
        pm.registerEvents(new PlayerQuit(), instance);
        pm.registerEvents(new EntityTarget(), instance);
        pm.registerEvents(new PlayerRespawn(), instance);
        pm.registerEvents(new EntityExplode(), instance);
        pm.registerEvents(new EntityDamageByEntity(), instance);
        pm.registerEvents(new PlayerJoin(), instance);
        pm.registerEvents(new BlockBreak(), instance);
        pm.registerEvents(new PlayerDropItem(), instance);
        pm.registerEvents(new PlayerKick(), instance);
        pm.registerEvents(new XMPP(), instance);
        /* COMMANDS */
        ((Ablockalypse) instance).getCommand("za").setExecutor(new BaseCommand());
    }
}
