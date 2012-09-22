package com.github.Ablockalypse.JamesNorris.Interface;

import org.bukkit.entity.Player;

import com.github.Ablockalypse.JamesNorris.Implementation.ZAGame;
import com.github.Ablockalypse.JamesNorris.Manager.SoundManager;
import com.github.Ablockalypse.JamesNorris.Util.Util.PowerupType;

public interface ZAPlayerInterface {
	public void addPoints(int i);

	public ZAGame getGame();

	public String getName();

	public Player getPlayer();

	public int getPoints();

	public SoundManager getSoundManager();

	public boolean isInLastStand();

	public void loadPlayerToGame(String name);

	public void sendToMainframe();

	public void subtractPoints(int i);

	public void toggleLastStand();

	public void givePowerup(PowerupType type);
}
