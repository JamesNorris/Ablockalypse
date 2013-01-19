package com.github.JamesNorris.Threading;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.ZASound;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Interface.ZAThread;
import com.github.JamesNorris.Util.MiscUtil;
import com.github.JamesNorris.Util.SoundUtil;

public class NextLevelThread extends DataManipulator implements ZAThread {
	private ZAGame game;
	private int counter, interval, count = 0;
	private String name;
	private boolean played, running, runThrough;

	public NextLevelThread(ZAGame game, boolean autorun, int interval) {
		this.game = game;
		name = game.getName();
		played = false;
		counter = 3;
		running = false;
		this.interval = interval;
		if (autorun)
			setRunThrough(true);
		data.thread.add(this);
	}

	public boolean isRunning() {
		return running;
	}

	@Override public boolean runThrough() {
		return runThrough;
	}

	@Override public void setRunThrough(boolean tf) {
		runThrough = tf;
	}

	@Override public void run() {
		running = true;
		if (game.isPaused())
			remove();
		if (data.gameExists(name) && game.hasStarted() && game.getMobCount() <= 0 && !game.isPaused()) {
			--counter;
			if (!played) {
				played = true;
				if (game.getLevel() != 0) {
					for (String s : game.getPlayers()) {
						ZAPlayer zap = data.findZAPlayer(Bukkit.getPlayer(s), game.getName());
						SoundUtil.generateSound(zap.getPlayer(), ZASound.PREV_LEVEL);
						Player p = zap.getPlayer();
						MiscUtil.sendPlayerMessage(p, ChatColor.BOLD + "Level " + ChatColor.RESET + ChatColor.RED + game.getLevel() + ChatColor.RESET + ChatColor.BOLD + " over... Next level: " + ChatColor.RED + (game.getLevel() + 1));
					}
					game.broadcastPoints();
				}
			}
			if (counter == 0) {
				played = false;
				if (game.isWolfRound())
					game.setWolfRound(false);
				game.nextLevel();
				for (String s : game.getPlayers()) {
					ZAPlayer zap = data.findZAPlayer(Bukkit.getPlayer(s), game.getName());
					SoundUtil.generateSound(zap.getPlayer(), ZASound.NEXT_LEVEL);
				}
				remove();
			}
		}
	}

	@Override public void remove() {
		count = 0;
		running = false;
		data.thread.remove(this);
	}

	@Override public int getCount() {
		return count;
	}

	@Override public int getInterval() {
		return interval;
	}

	@Override public void setCount(int i) {
		count = i;
	}

	@Override public void setInterval(int i) {
		interval = i;
	}
}
