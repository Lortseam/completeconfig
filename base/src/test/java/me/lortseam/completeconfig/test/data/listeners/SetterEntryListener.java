package me.lortseam.completeconfig.test.data.listeners;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;

public class SetterEntryListener implements ConfigContainer {

    @ConfigEntry
    private boolean value;

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

}
