package com.github.Ablockalypse.JamesNorris.Interface;

import org.bukkit.block.Block;

public interface AreaInterface {
	public Block getSignBlock();

	public boolean isPurchased();

	public boolean isWood();

	public void purchaseArea();

	public void replaceArea();

	public void safeReplace();

	public void setWood(boolean tf);
}
