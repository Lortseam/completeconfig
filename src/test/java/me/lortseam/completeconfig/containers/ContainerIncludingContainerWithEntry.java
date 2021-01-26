package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class ContainerIncludingContainerWithEntry implements ConfigEntryContainer {

    @Override
    public ConfigEntryContainer[] getTransitiveContainers() {
        return new ConfigEntryContainer[]{new ContainerWithEntry()};
    }

}
