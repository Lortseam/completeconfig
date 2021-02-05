package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;

public class POJOContainerWithEntry implements ConfigContainer {

    private boolean entry;

    @Override
    public boolean isConfigPOJO() {
        return true;
    }

}
