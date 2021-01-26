package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class ContainerWithContainerWithEntry implements ConfigEntryContainer {

    @Transitive
    private final ContainerWithEntry container = new ContainerWithEntry();

}
