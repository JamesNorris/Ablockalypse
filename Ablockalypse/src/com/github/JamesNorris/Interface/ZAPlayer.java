package com.github.JamesNorris.Interface;

import org.bukkit.entity.Player;

import com.github.JamesNorris.Manager.SoundManager;
import com.github.JamesNorris.Util.Util.PowerupType;

public interface ZAPlayer {
	public void addPoints(int i);

	public ZAGame getGame();

	public String getName();

	public Player getPlayer();

	public int getPoints();

	public SoundManager getSoundManager();

	public void givePowerup(PowerupType type);

	public boolean isInLastStand();

	public boolean isInLimbo();

	public void loadPlayerToGame(String name);

	public void removeFromGame();

	public void sendToMainframe();

	public void subtractPoints(int i);

	public void toggleLastStand();

	public void toggleLimbo();
}
