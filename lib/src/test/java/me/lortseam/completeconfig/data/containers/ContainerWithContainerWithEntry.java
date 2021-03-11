package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;

public class ContainerWithContainerWithEntry implements ConfigContainer {

    @Transitive
    private final ContainerWithEntry container = new ContainerWithEntry();

}
