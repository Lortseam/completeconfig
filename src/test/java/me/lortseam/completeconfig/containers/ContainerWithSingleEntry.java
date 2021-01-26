package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class ContainerWithSingleEntry implements ConfigEntryContainer {

    @ConfigEntry
    private boolean entry;
    private boolean noEntry;

}
