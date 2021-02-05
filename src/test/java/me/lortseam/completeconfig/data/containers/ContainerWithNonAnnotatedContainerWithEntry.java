package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class ContainerWithNonAnnotatedContainerWithEntry implements ConfigEntryContainer {

    private final ContainerWithEntry nonAnnotatedContainer = new ContainerWithEntry();

}
