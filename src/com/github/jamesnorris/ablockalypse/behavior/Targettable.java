package com.github.jamesnorris.ablockalypse.behavior;

import org.bukkit.Location;

import com.github.jamesnorris.ablockalypse.aspect.ZAMob;

public interface Targettable {
    public boolean isResponsive();

    public boolean isTargettedBy(ZAMob mob);

    public Location updateTarget();
}
