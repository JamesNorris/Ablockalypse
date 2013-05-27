package com.github.jamesnorris.implementation.serialized;

import org.bukkit.Location;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.util.SerializableLocation;

public class SerialBarrier implements Permadata {
    private static final long serialVersionUID = -6829577131094156987L;
    private final SerializableLocation serialCenter;
    private final SerialGame serialGame;
    private final boolean blinking;
    private final int fixtimesoriginal, fixtimes, hittimesoriginal, hittimes;

    public SerialBarrier(Barrier barrier) {
        this.serialCenter = new SerializableLocation(barrier.getCenter());
        this.serialGame = (SerialGame) barrier.getGame().getSerializedVersion();
        this.blinking = barrier.getBlinkerThread().isRunning();
        this.fixtimesoriginal = barrier.getFixRequirement();
        this.fixtimes = barrier.getFixTimes();
        this.hittimesoriginal = barrier.getHitRequirement();
        this.hittimes = barrier.getHitTimes();
    }
    
    public Location getLocation() {
        return SerializableLocation.returnLocation(serialCenter);
    }
    
    public Game getGame() {
        DataContainer data = Ablockalypse.getData();
        return (data.gameExists(serialGame.getName())) ? data.getGame(serialGame.getName(), true) : serialGame.load();
    }

    public Barrier load() {
        Location barrierLoc = getLocation();
        DataContainer data = Ablockalypse.getData();
        Barrier barrier = (data.isBarrier(barrierLoc)) ? data.getBarrier(barrierLoc) : new Barrier(barrierLoc, getGame());
        barrier.setBlinking(blinking);
        barrier.setFixRequirement(fixtimesoriginal);
        barrier.setFixTimes(fixtimes);
        barrier.setHitRequirement(hittimesoriginal);
        barrier.setHitTimes(hittimes);
        return barrier;
    }
}
