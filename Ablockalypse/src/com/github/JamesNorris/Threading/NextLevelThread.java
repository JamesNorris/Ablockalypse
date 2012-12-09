package com.github.JamesNorris.Threading;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Util.Enumerated.ZASound;
import com.github.JamesNorris.Util.SoundUtil;

public class NextLevelThread extends DataManipulator {
	private ZAGame game;
	private int id, counter, wait;
	private Ablockalypse instance;
	private boolean played, running;
	private String name;

	/**
	 * The thread for checking for next level, depending on remaining mobs.
	 * 
	 * @param game The game to run the thread for
	 * @param nextlevel Whether or not to run the thread automatically
	 * @param wait The amount of time between thread runs
	 */
	public NextLevelThread(ZAGame game, boolean nextlevel, int wait) {
		this.game = game;
		this.wait = wait;
		name = game.getName();
		instance = Ablockalypse.instance;
		played = false;
		counter = 3;
		running = false;
		if (nextlevel)
			waitForNextLevel();
	}

	/**
	 * Cancels the thread.
	 */
	public void cancel() {
		running = false;
		Bukkit.getScheduler().cancelTask(id);
	}

	public boolean isRunning() {
		return running;
	}

	/**
	 * Waits for the mobs to all be killed, then starts the next level.
	 */
	protected void waitForNextLevel() {
		running = true;
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override public void run() {
				if (game.isPaused())
					cancel();
				if (data.gameExists(name) && game.hasStarted() && game.getMobCount() <= 0 && !game.isPaused()) {
					--counter;
					if (!played) {
						played = true;
						if (game.getLevel() != 0) {
							for (String s : game.getPlayers()) {
								ZAPlayer zap = data.findZAPlayer(Bukkit.getPlayer(s), game.getName());
								SoundUtil.generateSound(zap.getPlayer(), ZASound.PREV_LEVEL);
								Player p = zap.getPlayer();
								p.sendMessage(ChatColor.BOLD + "Level " + ChatColor.RESET + ChatColor.RED + game.getLevel() + ChatColor.RESET + ChatColor.BOLD + " over... Next level: " + ChatColor.RED + (game.getLevel() + 1));
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
						cancel();
					}
				}
			}
		}, wait, wait);
	}
}
