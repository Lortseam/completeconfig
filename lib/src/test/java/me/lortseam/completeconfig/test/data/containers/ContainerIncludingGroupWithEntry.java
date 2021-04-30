package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

public class ContainerIncludingGroupWithEntry implements ConfigContainer {

    @Override
    public ConfigContainer[] getTransitives() {
        return new ConfigContainer[]{new ConfigGroup() {

            @ConfigEntry
            private boolean containerIncludingGroupWithEntryEntry;

        }};
    }

}
