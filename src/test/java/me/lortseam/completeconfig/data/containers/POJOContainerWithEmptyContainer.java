package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;

public class POJOContainerWithEmptyContainer implements ConfigContainer {

    private EmptyContainer container = new EmptyContainer();

    @Override
    public boolean isConfigObject() {
        return true;
    }

}
