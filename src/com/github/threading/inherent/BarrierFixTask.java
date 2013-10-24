package com.github.threading.inherent;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.block.Barrier;
import com.github.aspect.entity.ZAPlayer;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.enumerated.ZASound;
import com.github.threading.RepeatingTask;

public class BarrierFixTask extends RepeatingTask {
    private static List<String> fixers = new ArrayList<String>();
    private static final int INTERVAL = 20;
    private Barrier barrier;
    private Location center;
    private DataContainer data = Ablockalypse.getData();
    private Player player;
    private ZAPlayer zap;

    public BarrierFixTask(Barrier barrier, ZAPlayer zap, boolean autorun) {
        super(INTERVAL, autorun);
        String name = zap.getPlayer().getName();
        if (fixers.contains(name)) {
            return;
        }
        fixers.add(name);
        this.barrier = barrier;
        this.zap = zap;
        player = zap.getPlayer();
        center = barrier.getCenter();
    }

    @Override public void cancel() {
        if (player != null) {
            fixers.remove(player.getName());
        }
        data.objects.remove(this);
    }

    @Override public void run() {
        if (player == null || !player.isSneaking() || player.isDead() || !(barrier.getCenter().distanceSquared(player.getLocation()) < 4) || !(barrier.getHP() < 5)) {
            cancel();
            return;
        }
        barrier.setHP(barrier.getHP() + 1);
        if (barrier.getHP() < 5) {
            zap.addPoints((Integer) Setting.BARRIER_PART_FIX_PAY.getSetting());
        }
        ZASound.BARRIER_REPAIR.play(center);
        ZAEffect.WOOD_BREAK.play(center);
        if (barrier.getHP() == 5) {
            zap.addPoints((Integer) Setting.BARRIER_FULL_FIX_PAY.getSetting());
            barrier.replacePanels();
            cancel();
        }
    }
}
