package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class POJOContainerWithEmptyContainer implements ConfigEntryContainer {

    private EmptyContainer container = new EmptyContainer();

    @Override
    public boolean isConfigPOJO() {
        return true;
    }

}
