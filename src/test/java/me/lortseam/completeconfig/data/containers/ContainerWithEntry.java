package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class ContainerWithEntry implements ConfigEntryContainer {

    @ConfigEntry
    private boolean entry;

    public boolean getValue() {
        return entry;
    }

}
