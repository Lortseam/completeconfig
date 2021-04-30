package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;

public class ContainerIncludingContainerWithEntry implements ConfigContainer {

    @Override
    public ConfigContainer[] getTransitives() {
        return new ConfigContainer[]{new ConfigContainer() {

            @ConfigEntry
            private boolean containerIncludingContainerWithEntryEntry;

        }};
    }

}
