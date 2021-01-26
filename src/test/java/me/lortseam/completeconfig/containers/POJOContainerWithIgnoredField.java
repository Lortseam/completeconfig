package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class POJOContainerWithIgnoredField implements ConfigEntryContainer {

    @ConfigEntry.Ignore
    private boolean noEntry;

    @Override
    public boolean isConfigPOJO() {
        return true;
    }

}