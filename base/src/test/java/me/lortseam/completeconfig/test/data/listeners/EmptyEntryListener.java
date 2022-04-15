package me.lortseam.completeconfig.test.data.listeners;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;

public class EmptyEntryListener implements ConfigContainer {

    @ConfigEntry
    private boolean value;

    public void setValue(boolean value) {}

    public boolean getValue() {
        return value;
    }

}
