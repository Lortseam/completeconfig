package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;

public class ContainerWithStaticContainerWithEntry implements ConfigContainer {

    @Transitive
    private static final ConfigContainer container = new ConfigContainer() {

        @ConfigEntry
        private boolean cwscweEntry;

    };

}
