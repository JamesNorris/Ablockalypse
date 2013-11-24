package com.github.jamesnorris.ablockalypse.aspect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;

public class SpecificGameAspect extends NonspecificGameAspect {
    private Game game;
    
    public SpecificGameAspect(Game game, List<Location> locations) {
        this(game, locations, false);
    }
    
    public SpecificGameAspect(Game game, Location location) {
        this(game, location, false);
    }

    public SpecificGameAspect(Game game, List<Location> locations, boolean shouldBlink) {
        super(locations, shouldBlink);
        this.game = game;
        game.addObject(this);
    }

    @SuppressWarnings("serial") public SpecificGameAspect(Game game, final Location location, boolean shouldBlink) {
        this(game, new ArrayList<Location>() {
            {
                add(location);
            }
        }, shouldBlink);
    }

    @Override public Game getGame() {
        return game;
    }

    @Override public void remove() {
        game.removeObject(this);
        super.remove();
    }
}
