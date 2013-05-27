package com.github.jamesnorris.implementation.serialized;

import org.bukkit.Location;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.MysteryChest;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.util.SerializableLocation;

public class SerialMysteryChest implements Permadata {
    private static final long serialVersionUID = -5745340303290929566L;
    private final boolean active;
    private final SerialGame serialGame;
    private final SerializableLocation serialLocation;
    private final int uses;
    
    public SerialMysteryChest(MysteryChest chest) {
        this.active = chest.isActive();
        this.serialGame = (SerialGame) chest.getGame().getSerializedVersion();
        this.serialLocation = new SerializableLocation(chest.getLocation());
        this.uses = chest.getActiveUses();
    }
    
    public Game getGame() {
        DataContainer data = Ablockalypse.getData();
        return (data.gameExists(serialGame.getName())) ? data.getGame(serialGame.getName(), true) : serialGame.load();
    }
    
    public Location getLocation()  {
        return SerializableLocation.returnLocation(serialLocation);
    } 
    
    public boolean isActive() {
        return active;
    }
    
    public MysteryChest load() {
        Location chestLoc = getLocation();
        DataContainer data = Ablockalypse.getData();
        MysteryChest chest = (data.isMysteryChest(chestLoc)) ? data.getMysteryChest(chestLoc) : new MysteryChest(getGame(), chestLoc, isActive());
        chest.setActiveUses(uses);
        return chest;
    }
}
