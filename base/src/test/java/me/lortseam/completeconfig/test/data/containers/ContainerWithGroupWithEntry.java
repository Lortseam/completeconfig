package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

public class ContainerWithGroupWithEntry implements ConfigContainer {

    @Transitive
    private final ConfigGroup group = new ConfigGroup() {

        @ConfigEntry
        private boolean cwgweEntry;

    };

}
