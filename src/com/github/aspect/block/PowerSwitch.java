package com.github.aspect.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Lever;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.PermanentAspect;
import com.github.aspect.intelligent.Game;
import com.github.behavior.GameObject;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class PowerSwitch extends PermanentAspect implements GameObject {// now created when a zaplayer interacts with a switch
    private Game game;
    private Location location;
    private Lever lever;
    private BlockState state;
    private UUID uuid = UUID.randomUUID();
    private DataContainer data = Ablockalypse.getData();

    public PowerSwitch(Game game, Location location, Lever lever) {
        this.game = game;
        this.location = location;
        this.state = location.getBlock().getState();
        location.setPitch(0);
        location.setYaw(0);
        this.lever = lever;
        game.addObject(this);
        data.objects.add(this);
    }

    public PowerSwitch(SavedVersion savings) {
        this(Ablockalypse.getData().getGame((String) savings.get("game_name"), true), SerialLocation.returnLocation((SerialLocation) savings.get("location")), (Lever) SerialLocation.returnLocation((SerialLocation) savings.get("location")).getBlock().getState().getData());      
        Lever savedLever = (Lever) SerialLocation.returnLocation((SerialLocation) savings.get("location")).getBlock().getState().getData();
        if (savedLever != null) {
            savedLever.setPowered((Boolean) savings.get("is_powered"));
        }
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
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

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID().toString() + ">";
    }

    @Override public Block getDefiningBlock() {
        return location.getBlock();
    }

    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(getDefiningBlock());
        return blocks;
    }

    @Override public Game getGame() {
        return game;
    }

    @Override public void remove() {
        lever.setPowered(false);
        state.update(true, true);
        game.removeObject(this);
        data.objects.remove(this);
    }

    @Override public void onGameEnd() {
        remove();
    }

    @Override public void onGameStart() {
        lever.setPowered(false);
        state.update(true, true);
    }

    @Override public void onNextLevel() {}

    @Override public void onLevelEnd() {}
    
    @Override public int getLoadPriority() {
        return 2;
    }
}
