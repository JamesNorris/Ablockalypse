package com.github.iKeirNez.Util;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.zathrus_writer.commandsex.api.XMPPAPI;

public class XMPP {
	public enum XMPPType {
		LAST_STAND, PLAYER_JOIN_GAME, PLAYER_LEAVE_GAME, ZA_GAME_END, ZA_GAME_START
	}

	public static void send(final boolean send, final String message) {
		if (send)
			XMPPAPI.sendMessage(message);
	}

	public static void sendMessage(final String message, final XMPPType type) {
		final ConfigurationData cd = External.ym.getConfigurationData();
		if (External.CommandsEXPresent)
			if (XMPPAPI.isXMPPEnabled())
				switch (type) {
					case PLAYER_JOIN_GAME:
						send(cd.xmppPlayerJoin, message);
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
