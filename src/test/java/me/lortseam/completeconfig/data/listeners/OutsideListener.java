package me.lortseam.completeconfig.data.listeners;

import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.api.ConfigEntryListener;
import me.lortseam.completeconfig.data.containers.ContainerWithEntry;

public class OutsideListener implements ConfigEntryContainer {

    @Transitive
    private final ContainerWithEntry container = new ContainerWithEntry();
    private boolean value = container.getValue();

    @ConfigEntryListener(container = ContainerWithEntry.class, value = "entry")
    public void onUpdateValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

}
