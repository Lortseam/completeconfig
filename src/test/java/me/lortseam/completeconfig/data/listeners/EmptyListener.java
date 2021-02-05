package me.lortseam.completeconfig.data.listeners;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.api.ConfigEntryListener;

public class EmptyListener implements ConfigEntryContainer {

    @ConfigEntry
    private boolean value;

    @ConfigEntryListener("value")
    public void onUpdate(boolean value) {}

    public boolean getValue() {
        return value;
    }

}
