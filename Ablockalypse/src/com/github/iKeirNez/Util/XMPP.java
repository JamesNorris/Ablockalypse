package com.github.iKeirNez.Util;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Enumerated.Setting;
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
		if (External.CommandsEXPresent)
			if (XMPPAPI.isXMPPEnabled())
				switch (type) {
					case PLAYER_JOIN_GAME:
						send((Boolean) Setting.XMPPPLAYERJOIN.getSetting(), message);
					case PLAYER_LEAVE_GAME:
						send((Boolean) Setting.XMPPPLAYERLEAVE.getSetting(), message);
					break;
					case ZA_GAME_START:
						send((Boolean) Setting.XMPPGAMESTART.getSetting(), message);
					break;
					case ZA_GAME_END:
						send((Boolean) Setting.XMPPGAMEEND.getSetting(), message);
					break;
					case LAST_STAND:
						send((Boolean) Setting.XMPPLASTSTAND.getSetting(), message);
				}
	}
}
