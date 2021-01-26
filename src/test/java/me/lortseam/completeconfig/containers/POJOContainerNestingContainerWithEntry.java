package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class POJOContainerNestingContainerWithEntry implements ConfigEntryContainer {

    @Override
    public boolean isConfigPOJO() {
        return true;
    }

    public class ContainerWithEntry implements ConfigEntryContainer {

        @ConfigEntry
        private boolean entry;

    }

}
