package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;

public class ContainerWithStaticEntry implements ConfigContainer {

    @ConfigEntry
    private static boolean entry;

}
