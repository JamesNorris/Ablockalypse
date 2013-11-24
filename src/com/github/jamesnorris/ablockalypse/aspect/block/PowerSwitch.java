package com.github.jamesnorris.ablockalypse.aspect.block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.material.Lever;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.aspect.SpecificGameAspect;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.utility.serial.SavedVersion;
import com.github.jamesnorris.ablockalypse.utility.serial.SerialLocation;

public class PowerSwitch extends SpecificGameAspect {// now created when a zaplayer interacts with a switch
    private Game game;
    private Location location;
    private Lever lever;
    private BlockState state;
    private UUID uuid = UUID.randomUUID();

    public PowerSwitch(Game game, Location location, Lever lever) {
        super(game, location);
        this.game = game;
        this.location = location;
        state = location.getBlock().getState();
        location.setPitch(0);
        location.setYaw(0);
        this.lever = lever;
    }

    public PowerSwitch(SavedVersion savings) {
        this(Ablockalypse.getData().getGame((String) savings.get("game_name"), true), SerialLocation.returnLocation((SerialLocation) savings.get("location")), (Lever) SerialLocation.returnLocation((SerialLocation) savings.get("location")).getBlock().getState().getData());
        Lever savedLever = (Lever) SerialLocation.returnLocation((SerialLocation) savings.get("location")).getBlock().getState().getData();
        if (savedLever != null) {
            savedLever.setPowered((Boolean) savings.get("is_powered"));
        }
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
    }

    @Override public int getLoadPriority() {
        return 2;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> save = new HashMap<String, Object>();
        save.put("game_name", game.getName());
        save.put("location", new SerialLocation(location));
        save.put("is_powered", lever.isPowered());
        save.put("uuid", uuid);
        return new SavedVersion(getHeader(), save, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    @Override public void onGameEnd() {
        remove();
    }

    @Override public void onGameStart() {
        lever.setPowered(false);
        state.update(true, true);
    }

    @Override public void remove() {
        lever.setPowered(false);
        state.update(true, true);
        super.remove();
    }
}
