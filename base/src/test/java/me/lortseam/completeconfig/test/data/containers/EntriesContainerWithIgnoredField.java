package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;

@ConfigEntries(includeAll = true)
public class EntriesContainerWithIgnoredField implements ConfigContainer {

    @ConfigEntries.Exclude
    private boolean entriesContainerWithIgnoredFieldField;

}