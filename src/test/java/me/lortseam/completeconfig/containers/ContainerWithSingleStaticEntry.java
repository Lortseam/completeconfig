package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class ContainerWithSingleStaticEntry implements ConfigEntryContainer {

    @ConfigEntry
    private static boolean entry;

}
