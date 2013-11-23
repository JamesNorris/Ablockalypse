package com.github.queue;

import org.bukkit.event.player.PlayerInteractEvent;

public class QueuedPlayerInteractData extends QueuedData {
    private PlayerInteractEvent event = null;
    public static final String ANY_PLAYER = "ACCEPT=ANY_PLAYER";

    public QueuedPlayerInteractData(String playerName) {
        super(playerName);
    }

    // can return null
    public PlayerInteractEvent getPIE() {
        return event;
    }

    public boolean hasImportedPIE() {
        return event != null;
    }

    public void importPIE(PlayerInteractEvent event) {
        this.event = event;
    }

    public boolean isCompatible(PlayerInteractEvent event) {
        return event != null;
    }
}
