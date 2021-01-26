package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class POJOContainerNestingStaticContainerWithEntry implements ConfigEntryContainer {

    @Override
    public boolean isConfigPOJO() {
        return true;
    }

    public static class ContainerWithEntry implements ConfigEntryContainer {

        @ConfigEntry
        private boolean entry;

    }

}
