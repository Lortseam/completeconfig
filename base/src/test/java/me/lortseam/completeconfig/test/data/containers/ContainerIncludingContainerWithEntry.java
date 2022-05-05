package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;

import java.util.Collection;
import java.util.List;

public class ContainerIncludingContainerWithEntry implements ConfigContainer {

    @Override
    public Collection<ConfigContainer> getTransitives() {
        return List.of(new ConfigContainer() {

            @ConfigEntry
            private boolean containerIncludingContainerWithEntryEntry;

        });
    }

}
