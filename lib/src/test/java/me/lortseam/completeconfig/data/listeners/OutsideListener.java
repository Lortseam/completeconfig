package me.lortseam.completeconfig.data.listeners;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.containers.ContainerWithEntry;

public class OutsideListener implements ConfigContainer {

    @Transitive
    private final ContainerWithEntry container = new ContainerWithEntry();
    private boolean value = container.getValue();

    public void onUpdateValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

}
