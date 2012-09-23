package com.github.Ablockalypse.iKeirNez.Util;

import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Util.External;
import com.github.zathrus_writer.commandsex.api.XMPPAPI;

public class XMPP {
	public enum XMPPType {
		PLAYER_JOIN_GAME, PLAYER_LEAVE_GAME, ZA_GAME_START, ZA_GAME_END, LAST_STAND
	}

	public static void sendMessage(final String message, final XMPPType type) {
		final ConfigurationData cd = External.ym.getConfigurationData();
		if (External.CommandsEXPresent) {
			if (XMPPAPI.isXMPPEnabled()) {
				switch (type) {
					case PLAYER_JOIN_GAME:
						send(cd.xmppPlayerJoin, message);
					break;
					case PLAYER_LEAVE_GAME:
						send(cd.xmppPlayerLeave, message);
					break;
					case ZA_GAME_START:
						send(cd.xmppGameStart, message);
					break;
					case ZA_GAME_END:
						send(cd.xmppGameEnd, message);
					break;
					case LAST_STAND:
						send(cd.xmppLastStand, message);
				}
			}
		}
	}

	public static void send(final boolean send, final String message) {
		if (send) {
			XMPPAPI.sendMessage(message);
		}
	}
}
