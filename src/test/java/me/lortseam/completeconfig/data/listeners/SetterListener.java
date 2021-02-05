package me.lortseam.completeconfig.data.listeners;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.api.ConfigEntryListener;

public class SetterListener implements ConfigEntryContainer {

    @ConfigEntry
    private boolean value;

    @ConfigEntryListener
    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

}
