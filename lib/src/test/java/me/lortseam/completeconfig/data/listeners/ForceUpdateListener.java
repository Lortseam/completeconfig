package me.lortseam.completeconfig.data.listeners;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntryListener;

public class ForceUpdateListener implements ConfigContainer {

    @ConfigEntry(forceUpdate = true)
    private boolean value;

    @ConfigEntryListener("value")
    public void onUpdate(boolean value) {}

    public boolean getValue() {
        return value;
    }

}
