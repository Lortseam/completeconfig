package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class POJOContainerWithIgnoredField implements ConfigEntryContainer {

    @Ignore
    private boolean noEntry;

    @Override
    public boolean isConfigPOJO() {
        return true;
    }

}