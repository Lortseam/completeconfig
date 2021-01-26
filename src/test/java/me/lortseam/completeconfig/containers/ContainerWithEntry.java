package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class ContainerWithEntry implements ConfigEntryContainer {

    @ConfigEntry
    private boolean entry;

}
