package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class POJOContainerWithEntry implements ConfigEntryContainer {

    private boolean entry;

    @Override
    public boolean isConfigPOJO() {
        return true;
    }

}
