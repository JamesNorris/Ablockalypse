package com.github.jamesnorris.ablockalypse.enumerated;

import java.util.Map;

import com.github.jamesnorris.ablockalypse.aspect.ZACharacter;
import com.github.jamesnorris.ablockalypse.aspect.ZAPlayer;
import com.google.common.collect.Maps;

public enum PlayerStatus {
    LAST_STAND(1) {
        @Override public void set(ZACharacter character) {
            if (!(character instanceof ZAPlayer)) {
                return;
            }
            ZAPlayer zap = (ZAPlayer) character;
            if (!zap.isInLastStand()) {
                zap.toggleLastStand();
            }
        }
    },
    LIMBO(2) {
        @Override public void set(ZACharacter character) {
            character.setStatus(PlayerStatus.LIMBO);
        }
    },
    NORMAL(3) {
        @Override public void set(ZACharacter character) {
            if (character instanceof ZAPlayer) {
                ZAPlayer zap = (ZAPlayer) character;
                if (zap.isInLastStand()) {
                    zap.toggleLastStand();
                }
            }
            character.setStatus(PlayerStatus.NORMAL);
        }
    },
    TELEPORTING(4) {
        @Override public void set(ZACharacter character) {
            character.setStatus(PlayerStatus.TELEPORTING);
        }
    };
    private final static Map<Integer, PlayerStatus> BY_ID = Maps.newHashMap();
    static {
        for (PlayerStatus status : values()) {
            BY_ID.put(status.id, status);
        }
    }

    public static PlayerStatus getById(int id) {
        return BY_ID.get(id);
    }

    private int id;

    PlayerStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public abstract void set(ZACharacter character);
}
