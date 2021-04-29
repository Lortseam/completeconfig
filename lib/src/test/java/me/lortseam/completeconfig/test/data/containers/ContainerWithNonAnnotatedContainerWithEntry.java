package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;

public class ContainerWithNonAnnotatedContainerWithEntry implements ConfigContainer {

    private final ConfigContainer nonAnnotatedContainer = new ConfigContainer() {

        @ConfigEntry
        private boolean entry;

    };

}
