package me.lortseam.completeconfig.test.data.listeners;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;

public class ContainerListener implements ConfigContainer {

    @ConfigEntry
    private boolean value;
    private boolean called;

    @Override
    public void onUpdate() {
        called = true;
    }

    public boolean getValue() {
        return value;
    }

    public boolean isCalled() {
        return called;
    }

}
