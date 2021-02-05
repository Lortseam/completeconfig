package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;

public class ContainerIncludingContainerWithEntry implements ConfigContainer {

    @Override
    public ConfigContainer[] getTransitiveContainers() {
        return new ConfigContainer[]{new ContainerWithEntry()};
    }

}
