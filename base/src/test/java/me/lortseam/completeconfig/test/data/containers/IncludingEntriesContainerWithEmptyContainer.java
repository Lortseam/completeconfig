package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;

@ConfigEntries(includeAll = true)
public class IncludingEntriesContainerWithEmptyContainer implements ConfigContainer {

    private final ConfigContainer container = new ConfigContainer() {

    };

}
