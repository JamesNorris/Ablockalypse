package com.github.jamesnorris.ablockalypse.aspect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.behavior.MapDatable;
import com.github.jamesnorris.ablockalypse.enumerated.ZAEffect;
import com.github.jamesnorris.ablockalypse.utility.SerialLocation;

public class MobSpawner extends SpecificGameAspect implements MapDatable {
    private Game game;
    private Location loc;
    private UUID uuid = UUID.randomUUID();

    public MobSpawner(Location loc, Game game) {
        super(game, loc, !game.hasStarted());
        this.loc = loc;
        this.game = game;
        load();
    }

    public MobSpawner(Map<String, Object> savings) {
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

    @Override public Map<String, Object> getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("uuid", getUUID());
        savings.put("is_active", super.isPowered());
        savings.put("game_name", game.getName());
        savings.put("location", loc == null ? null : new SerialLocation(loc));
        return savings;
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
