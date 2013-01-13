package com.github.JamesNorris;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.JamesNorris.Enumerated.MessageDirection;
import com.github.JamesNorris.Enumerated.ZAEventType;
import com.github.JamesNorris.Event.GameCreateEvent;
import com.github.JamesNorris.Event.GameEndEvent;
import com.github.JamesNorris.Event.GameMobDeathEvent;
import com.github.JamesNorris.Event.GameMobSpawnEvent;
import com.github.JamesNorris.Event.GamePlayerJoinEvent;
import com.github.JamesNorris.Event.GamePlayerLeaveEvent;
import com.github.JamesNorris.Event.GameSignClickEvent;
import com.github.JamesNorris.Event.LastStandEvent;
import com.github.JamesNorris.Util.SpecificMessage;

public class XMPP implements Listener {
	private String gameString = "@game@";
	private String lastStandStatusString = "@laststandstatus@";
	private String levelString = "@level@";
	private String line2String = "@line2@";
	private String locationString = "@location@";
	private String mobCountString = "@mobcount@";
	private String playerString = "@player@";

	@EventHandler public void LISTEN(Event event) {
		for (ZAEventType type : ZAEventType.values()) {
			String base = type.getXMPPMessage();
			switch (type) {
				case GAMECREATEEVENT:
					GameCreateEvent gce = (GameCreateEvent) event;
					base = base.replaceAll(gameString, gce.getGame().getName());
					base = base.replaceAll(playerString, gce.getPlayer().getName());
				break;
				case GAMEENDEVENT:
					GameEndEvent gee = (GameEndEvent) event;
					base = base.replaceAll(gameString, gee.getGame().getName());
				break;
				case GAMEMOBDEATHEVENT:
					GameMobDeathEvent gmde = (GameMobDeathEvent) event;
					base = base.replaceAll(gameString, gmde.getGame().getName());
					base = base.replaceAll(mobCountString, gmde.getGame().getMobCount() + "");
					base = base.replaceAll(levelString, gmde.getGame().getLevel() + "");
				break;
				case GAMEMOBSPAWNEVENT:
					GameMobSpawnEvent gmse = (GameMobSpawnEvent) event;
					base = base.replaceAll(gameString, gmse.getGame().getName());
					base = base.replaceAll(mobCountString, gmse.getGame().getMobCount() + "");
					base = base.replaceAll(levelString, gmse.getGame().getLevel() + "");
				break;
				case GAMEPLAYERJOINEVENT:
					GamePlayerJoinEvent gpje = (GamePlayerJoinEvent) event;
					base = base.replaceAll(gameString, gpje.getGame().getName());
					base = base.replaceAll(playerString, gpje.getPlayer().getName());
				break;
				case GAMEPLAYERLEAVEEVENT:
					GamePlayerLeaveEvent gple = (GamePlayerLeaveEvent) event;
					base = base.replaceAll(gameString, gple.getGame().getName());
					base = base.replaceAll(playerString, gple.getPlayer().getName());
				break;
				case GAMESIGNCLICKEVENT:
					GameSignClickEvent gsce = (GameSignClickEvent) event;
					base = base.replaceAll(playerString, gsce.getPlayer().getName());
					base = base.replaceAll(line2String, gsce.getSign().getLine(1));// Line 1 in Java is actually line 2 in the game.
					Location loc = gsce.getSign().getLocation();
					base = base.replaceAll(locationString, loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
				break;
				case LASTSTANDEVENT:
					LastStandEvent lse = (LastStandEvent) event;
					base = base.replaceAll(gameString, lse.getGame().getName());
					base = base.replaceAll(playerString, lse.getPlayer().getName());
					String sat = (lse.isSitDown()) ? "knocked down" : "picked up";
					base = base.replaceAll(lastStandStatusString, sat);
				break;
				case MESSAGETRANSFEREVENT:
					break;//To prevent looping
			}
			MessageTransfer.sendMessage(MessageDirection.XMPP, new SpecificMessage(base));
		}
	}
}
