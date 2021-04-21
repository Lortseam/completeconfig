package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;

@ConfigEntries
public class EntriesContainerWithIgnoredField implements ConfigContainer {

    @ConfigEntries.Exclude
    private boolean noEntry;

}