package com.github.aspect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.github.aspect.intelligent.Game;

public class SpecificGameAspect extends NonspecificGameAspect {
    private Game game;

    public SpecificGameAspect(Game game, List<Location> locations) {
        super(locations);
        this.game = game;
        game.addObject(this);
    }

    @SuppressWarnings("serial") public SpecificGameAspect(Game game, final Location location) {
        this(game, new ArrayList<Location>() {
            {
                add(location);
            }
        });
    }

    @Override public Game getGame() {
        return game;
    }

    @Override public void remove() {
        game.removeObject(this);
        super.remove();
    }
}
