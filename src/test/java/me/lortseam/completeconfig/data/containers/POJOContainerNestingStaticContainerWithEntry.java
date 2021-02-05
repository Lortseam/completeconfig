package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;

public class POJOContainerNestingStaticContainerWithEntry implements ConfigContainer {

    @Override
    public boolean isConfigPOJO() {
        return true;
    }

    public static class ContainerWithEntry implements ConfigContainer {

        @ConfigEntry
        private boolean entry;

    }

}
