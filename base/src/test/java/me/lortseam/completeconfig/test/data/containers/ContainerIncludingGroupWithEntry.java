package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

import java.util.Collection;
import java.util.List;

public class ContainerIncludingGroupWithEntry implements ConfigContainer {

    @Override
    public Collection<ConfigContainer> getTransitives() {
        return List.of(new ConfigGroup() {

            @ConfigEntry
            private boolean containerIncludingGroupWithEntryEntry;

        });
    }

}
