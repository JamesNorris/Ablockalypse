package com.github.jamesnorris.implementation.serialized;

import org.bukkit.Location;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Passage;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.util.SerializableLocation;

public class SerialPassage implements Permadata {
    private static final long serialVersionUID = -1490620919545646410L;
    private final SerializableLocation serialLoc1, serialLoc2;
    private final boolean /*opened,*/ requiresPower, powered;//opened and powered are the same for passage
    private final SerialGame serialGame;
    
    public SerialPassage(Passage passage) {
        this.serialLoc1 = new SerializableLocation(passage.getPoint(1));
        this.serialLoc2 = new SerializableLocation(passage.getPoint(2));
        this.requiresPower = passage.requiresPower();
        this.powered = passage.isPowered();
        this.serialGame = (SerialGame) passage.getGame().getSerializedVersion();
    }
    
    public Game getGame() {
        DataContainer data = Ablockalypse.getData();
        return (data.gameExists(serialGame.getName())) ? data.getGame(serialGame.getName(), true) : serialGame.load();
    }
    
    public Location getLocation1() {
        return SerializableLocation.returnLocation(serialLoc1);
    }
    
    public Location getLocation2() {
        return SerializableLocation.returnLocation(serialLoc2);
    }
    
    public Passage load() {
        Location passageLoc1 = getLocation1();
        Location passageLoc2 = getLocation2();
        DataContainer data = Ablockalypse.getData();
        Passage passage = (data.isPassage(passageLoc1)) ? data.getPassage(passageLoc1) : new Passage(getGame(), passageLoc1, passageLoc2);
        passage.setRequiresPower(requiresPower);
        passage.power(powered);
        return passage;
    }
}
