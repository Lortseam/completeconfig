package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;

@ConfigEntries
public class EntriesContainerWithEmptyContainer implements ConfigContainer {

    private final EmptyContainer emptyContainer = new EmptyContainer();

}
