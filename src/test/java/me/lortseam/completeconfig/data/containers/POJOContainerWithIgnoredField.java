package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;

public class POJOContainerWithIgnoredField implements ConfigContainer {

    @Ignore
    private boolean noEntry;

    @Override
    public boolean isConfigPOJO() {
        return true;
    }

}