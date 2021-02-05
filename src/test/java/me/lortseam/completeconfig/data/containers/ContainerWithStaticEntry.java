package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class ContainerWithStaticEntry implements ConfigEntryContainer {

    @ConfigEntry
    private static boolean entry;

}
