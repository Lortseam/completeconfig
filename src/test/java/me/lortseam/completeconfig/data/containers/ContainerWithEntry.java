package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;

public class ContainerWithEntry implements ConfigContainer {

    @ConfigEntry
    private boolean entry;

    public boolean getValue() {
        return entry;
    }

}
