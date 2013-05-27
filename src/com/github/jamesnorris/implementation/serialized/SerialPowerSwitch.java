package com.github.jamesnorris.implementation.serialized;

import org.bukkit.Location;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.PowerSwitch;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.util.SerializableLocation;

public class SerialPowerSwitch implements Permadata {
    private static final long serialVersionUID = 2390652008324298908L;
    private final int cost;
    private final SerialGame serialGame;
    private final SerializableLocation serialLocation;
    private final boolean on;
    
    public SerialPowerSwitch(PowerSwitch power) {
        this.cost = power.getCost();
        this.serialGame = (SerialGame) power.getGame().getSerializedVersion();
        this.serialLocation = new SerializableLocation(power.getLocation());
        this.on = power.isOn();
    }
    
    public Game getGame() {
        DataContainer data = Ablockalypse.getData();
        return (data.gameExists(serialGame.getName())) ? data.getGame(serialGame.getName(), true) : serialGame.load();
    }
    
    public Location getLocation() {
        return SerializableLocation.returnLocation(serialLocation);
    }
    
    public int getCost() {
        return cost;
    }
    
    public PowerSwitch load() {
        Location powerLoc = getLocation();
        DataContainer data = Ablockalypse.getData();
        PowerSwitch power = (data.isPowerSwitch(powerLoc)) ? data.getPowerSwitch(powerLoc) : new PowerSwitch(getGame(), powerLoc, getCost());
        power.setOn(on);
        return power;
    }
}
