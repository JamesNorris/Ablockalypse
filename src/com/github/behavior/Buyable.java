package com.github.behavior;

public interface Buyable extends Shop {
    public boolean isBought();

    public void setBought(boolean bought);
}
