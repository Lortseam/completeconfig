package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class ContainerWithNonAnnotatedContainerWithEntry implements ConfigEntryContainer {

    private final ContainerWithEntry nonAnnotatedContainer = new ContainerWithEntry();

}
