package com.github.behavior;

public interface Powerable extends GameObject {
    public boolean isPowered();

    public void power(boolean power);

    public void powerOff();

    public void powerOn();

    public boolean requiresPower();

    public boolean requiresPurchaseFirst();

    public void setRequiresPower(boolean required);

    public void setRequiresPurchaseFirst(boolean purchase);
}
