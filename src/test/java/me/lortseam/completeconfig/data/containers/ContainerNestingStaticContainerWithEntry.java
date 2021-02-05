package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class ContainerNestingStaticContainerWithEntry implements ConfigEntryContainer {

    public static class ContainerWithEntry implements ConfigEntryContainer {

        @ConfigEntry
        private boolean entry;

    }

}
