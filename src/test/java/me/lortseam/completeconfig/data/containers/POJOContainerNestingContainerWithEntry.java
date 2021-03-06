package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;

public class POJOContainerNestingContainerWithEntry implements ConfigContainer {

    @Override
    public boolean isConfigObject() {
        return true;
    }

    public class ContainerWithEntry implements ConfigContainer {

        @ConfigEntry
        private boolean entry;

    }

}
