package com.github.JamesNorris.Implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Interface.Barrier;
import com.github.JamesNorris.Interface.Blinkable;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.Enumerated.ZAColor;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;
import com.github.JamesNorris.Util.Enumerated.ZASound;
import com.github.JamesNorris.Util.MathAssist;
import com.github.JamesNorris.Util.SoundUtil;
import com.github.JamesNorris.Util.Square;

public class GameBarrier implements Barrier, GameObject, Blinkable {
	private ArrayList<Block> blocks = new ArrayList<Block>();
	private Location center, spawnloc;
	private int radius, blockamt, id, id2;
	private int hittimesoriginal = 5, fixtimesoriginal = 3;
	private int hittimes;
	private int fixtimes;
	private ZAGameBase game;
	private Square square;
	private BlinkerThread bt;
	private ConfigurationData cd;
	private boolean correct;
	private Player p;

	/**
	 * Creates a new instance of a Barrier, where center is the center of the 3x3 barrier.
	 * 
	 * @param center The center of the barrier
	 */
	public GameBarrier(Block center, ZAGameBase game) {
		GlobalData.objects.add(this);
		this.center = center.getLocation();
		this.game = game;
		radius = 2;
		fixtimes = fixtimesoriginal;
		hittimes = hittimesoriginal;
		Random rand = new Random();
		/* finding spawnloc */
		int chance = rand.nextInt(4);
		World w = this.center.getWorld();
		int x = this.center.getBlockX();
		int y = this.center.getBlockY();
		int z = this.center.getBlockZ();
		int modX = rand.nextInt(2) + 3;
		int modZ = rand.nextInt(2) + 3;
		if (chance == 1) {
			x = x - modX;
			z = z - modZ;
		} else if (chance == 2)
			x = x - modX;
		else if (chance == 3)
			z = z - modZ;
		spawnloc = w.getBlockAt(x, y, z).getLocation();
		if (spawnloc == null)
			Ablockalypse.getMaster().crash(Ablockalypse.instance, "A barrier has been created that doesn't have a suitable mob spawn location nearby. This could cause NullPointerExceptions in the future!", false);
		/* end finding spawnloc */
		game.addBarrier(this);
		Location l = center.getLocation();
		if (!GlobalData.barriers.containsKey(l))
			GlobalData.barriers.put(center.getLocation(), game.getName());
		if (!GlobalData.gamebarriers.contains(this))
			GlobalData.gamebarriers.add(this);
		Square s = new Square(center.getLocation(), 1);
		for (Location loc : s.getLocations()) {
			Block b = loc.getBlock();
			if (b != null && !b.isEmpty() && b.getType() != null && b.getType() == Material.FENCE) {
				blocks.add(b);
				if (!GlobalData.barrierpanels.containsValue(loc))
					GlobalData.barrierpanels.put(this, loc);
				++blockamt;
			}
		}
		cd = External.getYamlManager().getConfigurationData();
		ArrayList<Block> blocks = new ArrayList<Block>();
		blocks.add(this.center.getBlock());
		bt = new BlinkerThread(blocks, ZAColor.BLUE, cd.blinkers, 30, this);
		if (blockamt == 9) {
			bt.setColor(ZAColor.GREEN);
			correct = true;
		} else {
			bt.setColor(ZAColor.RED);
			correct = false;
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Ablockalypse.instance, new Runnable() {
			@Override public void run() {
				bt.setColor(ZAColor.BLUE);
			}
		}, 200);
	}

	/**
	 * Gets the blocks that defines this object as an object.
	 * 
	 * @return The blocks assigned to this object
	 */
	public Block[] getDefiningBlocks() {
		return (Block[]) blocks.toArray();
	}

	/**
	 * Changes all blocks within the barrier to air.
	 * 
	 * @param c The creature that is breaking the barrier
	 */
	@Override public void breakBarrier(final Creature c) {
		breakBarrier((LivingEntity) c);
	}

	/**
	 * Slowly breaks the blocks of the barrier.
	 * 
	 * @param e The entityliving that is breaking the barrier
	 */
	@Override public void breakBarrier(final LivingEntity e) {
		if (bt.isRunning())
			bt.cancel();
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
			@Override public void run() {
				if (!e.isDead() && isWithinRadius(e) && !isBroken()) {
					--hittimes;
					SoundUtil.generateSound(center.getWorld(), center, ZASound.BARRIER_BREAK);
					EffectUtil.generateEffect(e.getWorld(), center, ZAEffect.WOOD_BREAK);
					if (hittimes == 0) {
						hittimes = hittimesoriginal;
						breakPanels();
						cancelBreak();
					}
				} else
					cancelBreak();
			}
		}, 100, 100);
	}

	/**
	 * Slowly breaks the blocks of the barrier.
	 * 
	 * @param p The player that is breaking the barrier
	 */
	@Override public void breakBarrier(Player p) {
		breakBarrier((LivingEntity) p);
	}

	/**
	 * Changes all blocks within the barrier to air.
	 */
	@Override public void breakPanels() {
		for (int i = 0; i <= blocks.size(); i++) {
			Block b = blocks.iterator().next();
			blocks.remove(b);
			b.setType(Material.AIR);
			EffectUtil.generateEffect(b.getWorld(), b.getLocation(), ZAEffect.SMOKE);
			blocks.add(b);
		}
	}

	/*
	 * Cancels the breakbarrier task.
	 */
	private void cancelBreak() {
		Bukkit.getScheduler().cancelTask(id);
	}

	/*
	 * Cancels the fixbarrier task.
	 */
	private void cancelFix() {
		Bukkit.getScheduler().cancelTask(id2);
	}

	/**
	 * Slowly fixes the blocks of the barrier.
	 * 
	 * @param c The creature that is fixing the barrier
	 */
	@Override public void fixBarrier(final Creature c) {
		fixBarrier((LivingEntity) c);
	}

	/**
	 * Slowly fixes the blocks of the barrier.
	 * 
	 * @param e The livingentity that is fixing the barrier
	 */
	@Override public void fixBarrier(final LivingEntity e) {
		if (bt.isRunning())
			bt.cancel();
		if (e instanceof Player)
			p = (Player) e;
		if (GlobalData.playerExists(p)) {
			final ZAPlayer zap = GlobalData.getZAPlayer(p);
			id2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
				@Override public void run() {
					if (p != null && !p.isSneaking())
						cancelFix();
					if (!e.isDead() && isWithinRadius(e) && isBroken()) {
						--fixtimes;
						if (fixtimes > 0)
							zap.addPoints(cd.barrierpartfix);
						SoundUtil.generateSound(center.getWorld(), center, ZASound.BARRIER_REPAIR);
						EffectUtil.generateEffect(e.getWorld(), center, ZAEffect.WOOD_BREAK);
						if (fixtimes == 0) {
							zap.addPoints(cd.barrierfullfix);
							fixtimes = fixtimesoriginal;
							replacePanels();
							cancelFix();
						}
					} else
						cancelFix();
				}
			}, 20, 20);
		}
	}

	/**
	 * Slowly fixes the blocks of the barrier.
	 * 
	 * @param p The player that is fixing the barrier
	 */
	@Override public void fixBarrier(Player p) {
		fixBarrier((LivingEntity) p);
	}

	/**
	 * Gets the BlinkerThread attached to this instance.
	 * 
	 * @return The BlinkerThread attached to this instance
	 */
	@Override public BlinkerThread getBlinkerThread() {
		return bt;
	}

	/**
	 * Returns the list of blocks in the barrier.
	 * 
	 * @return A list of blocks located in the barrier
	 */
	@Override public List<Block> getBlocks() {
		ArrayList<Block> bls = new ArrayList<Block>();
		for (Block b : blocks)
			bls.add(b);
		return bls;
	}

	/**
	 * Gets the center location of the barrier.
	 * 
	 * @return The center of the barrier
	 */
	@Override public Location getCenter() {
		return center;
	}

	/**
	 * Gets the game this barrier is involved in.
	 * 
	 * @return The game this barrier is attached to
	 */
	@Override public ZAGame getGame() {
		return game;
	}

	/**
	 * Gets the radius of the barrier as an integer.
	 * 
	 * @return The radius of the barrier
	 */
	@Override public int getRadius() {
		return radius;
	}

	/**
	 * Gets the mob spawn location for this barrier.
	 * 
	 * @return The mob spawn location around this barrier
	 */
	@Override public Location getSpawnLocation() {
		return spawnloc;
	}

	/**
	 * Gets the square surrounding this barrier for 2 blocks.
	 * 
	 * @return The barriers' surrounding square
	 */
	@Override public Square getSquare() {
		return square;
	}

	/**
	 * Tells whether or not the barrier has any missing blocks.
	 * 
	 * @return Whether or not the barrier is broken
	 */
	@Override public boolean isBroken() {
		if (center.getBlock().isEmpty())
			return true;
		return false;
	}

	/**
	 * Checks if the barrier is setup correctly or not.
	 * 
	 * @return Whether or not the barrier is setup correctly
	 */
	@Override public boolean isCorrect() {
		return correct;
	}

	/**
	 * Checks if the entity is within the radius of the barrier.
	 * 
	 * @param e The entity to check for
	 * @return Whether or not the entity is within the radius
	 */
	@Override public boolean isWithinRadius(Entity e) {
		Location el = e.getLocation();
		int x = el.getBlockX(), x2 = center.getBlockX();
		int y = el.getBlockY(), y2 = center.getBlockY();
		int z = el.getBlockZ(), z2 = center.getBlockZ();
		int distance = (int) MathAssist.distance(x, y, z, x2, y2, z2);
		if (distance <= radius)
			return true;
		return false;
	}

	/**
	 * Removes the barrier.
	 */
	@Override public void remove() {
		replacePanels();
		if (bt.isRunning())
			setBlinking(false);
		game.removeBarrier(this);
		GlobalData.barriers.remove(center);
		GlobalData.barrierpanels.remove(this);
		GlobalData.objects.remove(this);
	}

	/**
	 * Replaces all holes in the barrier.
	 */
	@Override public void replacePanels() {
		for (int i = 0; i <= blocks.size(); i++) {
			Block b = blocks.iterator().next();
			blocks.remove(b);
			b.setType(Material.FENCE);
			EffectUtil.generateEffect(b.getWorld(), b.getLocation(), ZAEffect.SMOKE);
			blocks.add(b);
		}
		SoundUtil.generateSound(center.getWorld(), center, ZASound.BARRIER_REPAIR);
	}

	/**
	 * Stops/Starts the blinker for this barrier.
	 * 
	 * @param tf Whether or not this barrier should blink
	 */
	@Override public void setBlinking(boolean tf) {
		if (bt.isRunning())
			bt.cancel();
		if (tf)
			bt.blink();
	}

	/**
	 * Sets the amount of fix rounds to wait before fixing the barrier.
	 * Fix rounds are called once every 20 ticks by all sneaking players near barriers.
	 * 
	 * @param i The amount of fix rounds to wait
	 */
	@Override public void setFixRequirement(int i) {
		fixtimesoriginal = i;
		fixtimes = i;
	}

	/**
	 * Sets the amount of hit rounds to wait before breaking the barrier.
	 * Hit rounds are called once every 100 ticks by all mobs close to barriers.
	 * 
	 * @param i The amount of hit rounds to wait
	 */
	@Override public void setHitRequirement(int i) {
		hittimesoriginal = i;
		hittimes = i;
	}

	/**
	 * Sets the radius of the barrier to be broken.
	 * 
	 * @param i The radius
	 */
	@Override public void setRadius(int i) {
		radius = i;
	}
}
