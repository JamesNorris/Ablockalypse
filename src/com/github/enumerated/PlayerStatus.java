package com.github.enumerated;

import java.util.Map;

import com.github.aspect.ZAPlayer;
import com.google.common.collect.Maps;

public enum PlayerStatus {
    LAST_STAND(1) {
        @Override public void set(ZAPlayer zap) {
            if (!zap.isInLastStand()) {
                zap.toggleLastStand();
            }
        }
    },
    LIMBO(2) {
        @Override public void set(ZAPlayer zap) {
            if (!zap.isInLimbo()) {
                zap.setStatus(PlayerStatus.LIMBO);
            }
        }
    },
    //@formatter:off
    NORMAL(3) {
        @Override public void set(ZAPlayer zap) {
            if (zap.isInLastStand()) {
                zap.toggleLastStand();
            }
            zap.setStatus(PlayerStatus.NORMAL);
        }        
    }, 
	TELEPORTING(4) {
        @Override public void set(ZAPlayer zap) {
            if (!zap.isTeleporting()) {
                zap.setStatus(PlayerStatus.TELEPORTING);
            }
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

    //@formatter:on
    //
    public abstract void set(ZAPlayer zap);
}
