package me.lortseam.completeconfig.data.listeners;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntryListener;

public class CustomListener implements ConfigContainer {

    @ConfigEntry
    private boolean value;

    @ConfigEntryListener("value")
    public void update(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

}
