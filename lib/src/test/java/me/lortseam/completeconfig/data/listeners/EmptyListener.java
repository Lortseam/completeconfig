package me.lortseam.completeconfig.data.listeners;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;

public class EmptyListener implements ConfigContainer {

    @ConfigEntry
    private boolean value;

    public void setValue(boolean value) {}

    public boolean getValue() {
        return value;
    }

}
