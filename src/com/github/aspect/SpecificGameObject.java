package com.github.aspect;

import org.bukkit.Location;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.intelligent.Game;

public class SpecificGameObject extends NonspecificGameObject {
    protected DataContainer data = Ablockalypse.getData();
    private Game game;
    
    public SpecificGameObject(Game game, Location location) {
        super(location);
        this.game = game;
    }

    @Override public Game getGame() {
        return game;
    }   
}
