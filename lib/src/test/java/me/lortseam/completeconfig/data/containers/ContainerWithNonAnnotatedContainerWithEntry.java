package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;

public class ContainerWithNonAnnotatedContainerWithEntry implements ConfigContainer {

    private final ContainerWithEntry nonAnnotatedContainer = new ContainerWithEntry();

}
