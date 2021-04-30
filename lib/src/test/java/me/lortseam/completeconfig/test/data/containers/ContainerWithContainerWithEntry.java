package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;

public class ContainerWithContainerWithEntry implements ConfigContainer {

    @Transitive
    private final ConfigContainer container = new ConfigContainer() {

        @ConfigEntry
        private boolean containerWithContainerWithEntryEntry;

    };

}
