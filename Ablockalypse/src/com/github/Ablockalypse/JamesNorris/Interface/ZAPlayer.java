package com.github.Ablockalypse.JamesNorris.Interface;

import org.bukkit.entity.Player;

import com.github.Ablockalypse.JamesNorris.Implementation.ZAGameBase;
import com.github.Ablockalypse.JamesNorris.Manager.SoundManager;
import com.github.Ablockalypse.JamesNorris.Util.Util.PowerupType;

public interface ZAPlayer {
	public void addPoints(int i);

	public ZAGameBase getGame();

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
