package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;

public class ContainerWithStaticEntry implements ConfigContainer {

    @ConfigEntry
    private static boolean entry;

}
