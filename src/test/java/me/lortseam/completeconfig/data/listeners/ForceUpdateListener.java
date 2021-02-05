package me.lortseam.completeconfig.data.listeners;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.api.ConfigEntryListener;

public class ForceUpdateListener implements ConfigEntryContainer {

    @ConfigEntry(forceUpdate = true)
    private boolean value;

    @ConfigEntryListener("value")
    public void onUpdate(boolean value) {}

    public boolean getValue() {
        return value;
    }

}
