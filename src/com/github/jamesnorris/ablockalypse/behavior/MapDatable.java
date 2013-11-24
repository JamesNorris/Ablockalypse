package com.github.jamesnorris.ablockalypse.behavior;

import org.bukkit.Location;

public interface MapDatable extends Cloneable {
    public Location getPointClosestToOrigin();

    public void paste(Location pointClosestToOrigin);

    public void remove();
}
