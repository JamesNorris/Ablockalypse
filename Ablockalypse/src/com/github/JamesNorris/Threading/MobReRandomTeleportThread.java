package com.github.JamesNorris.Threading;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.ZAEffect;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAThread;
import com.github.JamesNorris.Util.EffectUtil;

public class MobReRandomTeleportThread extends DataManipulator implements ZAThread {
	private boolean runThrough = false;
	private int count = 0, interval;
	private Creature c;
	private ZAGame game;

	public MobReRandomTeleportThread(Creature c, ZAGame game, boolean autorun, int interval) {
		setRunThrough(autorun);
		this.interval = interval;
		this.c = c;
		this.game = game;
		data.thread.add(this);
	}

	@Override public boolean runThrough() {
		return runThrough;
	}

	@Override public void setRunThrough(boolean tf) {
		runThrough = tf;
	}

	@Override public void run() {
		if (!c.isDead() && data.isZAMob((Entity) c)) {
			Location target = game.getRandomLivingPlayer().getLocation();
			Location strike = game.getSpawnManager().findSpawnLocation(target, 5, 3);
			c.teleport(strike);
			EffectUtil.generateEffect(strike.getWorld(), strike, ZAEffect.LIGHTNING);
		} else
			remove();
	}

	@Override public void remove() {
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
