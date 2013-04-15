package com.github.jamesnorris;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAEventType;
import com.github.jamesnorris.event.AblockalypseEvent;
import com.github.jamesnorris.event.GameCreateEvent;
import com.github.jamesnorris.event.GameEndEvent;
import com.github.jamesnorris.event.GameMobDeathEvent;
import com.github.jamesnorris.event.GameMobSpawnEvent;
import com.github.jamesnorris.event.GamePlayerJoinEvent;
import com.github.jamesnorris.event.GamePlayerLeaveEvent;
import com.github.jamesnorris.event.GameSignClickEvent;
import com.github.jamesnorris.event.LastStandEvent;
import com.github.zathrus_writer.commandsex.api.XMPPAPI;

public class XMPP implements Listener {
    private String gameString = "@game@";
    private String lastStandStatusString = "@laststandstatus@";
    private String levelString = "@level@";
    private String line2String = "@line2@";
    private String locationString = "@location@";
    private String mobCountString = "@mobcount@";
    private String playerString = "@player@";

    @EventHandler public void LISTEN(AblockalypseEvent event) {
        for (ZAEventType type : ZAEventType.values()) {
            String base = type.getXMPPMessage();
            boolean shouldSend = true;
            switch (type) {
                case GAME_CREATE_EVENT:
                    shouldSend = (Boolean) Setting.XMPP_GAME_CREATE.getSetting();
                    GameCreateEvent gce = (GameCreateEvent) event;
                    base = base.replaceAll(gameString, gce.getGame().getName());
                    base = base.replaceAll(playerString, gce.getPlayer().getName());
                break;
                case GAME_END_EVENT:
                    shouldSend = (Boolean) Setting.XMPP_GAME_END.getSetting();
                    GameEndEvent gee = (GameEndEvent) event;
                    base = base.replaceAll(gameString, gee.getGame().getName());
                break;
                case GAME_MOB_DEATH_EVENT:
                    shouldSend = (Boolean) Setting.XMPP_MOB_DEATH.getSetting();
                    GameMobDeathEvent gmde = (GameMobDeathEvent) event;
                    base = base.replaceAll(gameString, gmde.getGame().getName());
                    base = base.replaceAll(mobCountString, gmde.getGame().getMobCount() + "");
                    base = base.replaceAll(levelString, gmde.getGame().getLevel() + "");
                break;
                case GAME_MOB_SPAWN_EVENT:
                    shouldSend = (Boolean) Setting.XMPP_MOB_SPAWN.getSetting();
                    GameMobSpawnEvent gmse = (GameMobSpawnEvent) event;
                    base = base.replaceAll(gameString, gmse.getGame().getName());
                    base = base.replaceAll(mobCountString, gmse.getGame().getMobCount() + "");
                    base = base.replaceAll(levelString, gmse.getGame().getLevel() + "");
                break;
                case GAME_PLAYER_JOIN_EVENT:
                    shouldSend = (Boolean) Setting.XMPP_PLAYER_JOIN.getSetting();
                    GamePlayerJoinEvent gpje = (GamePlayerJoinEvent) event;
                    base = base.replaceAll(gameString, gpje.getGame().getName());
                    base = base.replaceAll(playerString, gpje.getPlayer().getName());
                break;
                case GAME_PLAYER_LEAVE_EVENT:
                    shouldSend = (Boolean) Setting.XMPP_PLAYER_LEAVE.getSetting();
                    GamePlayerLeaveEvent gple = (GamePlayerLeaveEvent) event;
                    base = base.replaceAll(gameString, gple.getGame().getName());
                    base = base.replaceAll(playerString, gple.getPlayer().getName());
                break;
                case GAME_SIGN_CLICK_EVENT:
                    shouldSend = (Boolean) Setting.XMPP_SIGN_CLICK.getSetting();
                    GameSignClickEvent gsce = (GameSignClickEvent) event;
                    base = base.replaceAll(playerString, gsce.getPlayer().getName());
                    base = base.replaceAll(line2String, gsce.getSign().getLine(1));// Line 1 in Java is actually line 2 in the game.
                    Location loc = gsce.getSign().getLocation();
                    base = base.replaceAll(locationString, loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
                break;
                case GAME_PLAYER_LAST_STAND_EVENT:
                    shouldSend = (Boolean) Setting.XMPP_LAST_STAND.getSetting();
                    LastStandEvent lse = (LastStandEvent) event;
                    base = base.replaceAll(gameString, lse.getGame().getName());
                    base = base.replaceAll(playerString, lse.getPlayer().getName());
                    String sat = (lse.isSitDown()) ? "knocked down" : "picked up";
                    base = base.replaceAll(lastStandStatusString, sat);
                break;
            }
            Plugin CommandsEX = Bukkit.getPluginManager().getPlugin("CommandsEX");
            shouldSend = (shouldSend) ? (CommandsEX != null && CommandsEX.isEnabled()) : false;
            if (shouldSend) {
                XMPPAPI.sendMessage(base);
            }
        }
    }
}
