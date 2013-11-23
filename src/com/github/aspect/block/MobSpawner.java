package com.github.aspect.block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.aspect.SpecificGameAspect;
import com.github.aspect.intelligent.Game;
import com.github.behavior.MapDatable;
import com.github.enumerated.ZAEffect;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class MobSpawner extends SpecificGameAspect implements MapDatable {
    private Game game;
    private Location loc;
    private UUID uuid = UUID.randomUUID();

    public MobSpawner(Location loc, Game game) {
        super(game, loc);
        this.loc = loc;
        this.game = game;
        setBlinking(!game.hasStarted());
    }

    public MobSpawner(SavedVersion savings) {
        this(SerialLocation.returnLocation((SerialLocation) savings.get("location")), Ablockalypse.getData().getGame((String) savings.get("game_name"), true));
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
    }

    public Block getBukkitBlock() {
        return loc.getBlock();
    }

    public Location getBukkitLocation() {
        return loc;
    }

    @Override public int getLoadPriority() {
        return 2;
    }

    @Override public Location getPointClosestToOrigin() {
        return loc;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("uuid", getUUID());
        savings.put("is_active", super.isPowered());
        savings.put("game_name", game.getName());
        savings.put("location", loc == null ? null : new SerialLocation(loc));
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    public boolean isActive() {
        return super.isPowered();
    }

    @Override public void onGameEnd() {
        setBlinking(true);
    }

    @Override public void onGameStart() {
        setBlinking(false);
    }

    @Override public void paste(Location pointClosestToOrigin) {
        loc = pointClosestToOrigin;
        refreshBlinker();
    }

    public void playEffect(ZAEffect effect) {
        effect.play(loc);
    }

    public void setActive(boolean tf) {
        super.setPowered(tf);
    }

    public void setBlock(Material m) {
        loc.getBlock().setType(m);
    }
}
