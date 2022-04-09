package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;

public class ContainerNestingStaticContainerWithEntry implements ConfigContainer {

    @Transitive
    public static class ContainerWithEntry implements ConfigContainer {

        @ConfigEntry
        private boolean containerNestingStaticContainerWithEntryEntry;

    }

}
