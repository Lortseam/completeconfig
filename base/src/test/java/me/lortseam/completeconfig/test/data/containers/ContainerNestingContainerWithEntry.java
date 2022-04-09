package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;

public class ContainerNestingContainerWithEntry implements ConfigContainer {

    @Transitive
    public class ContainerWithEntry implements ConfigContainer {

        @ConfigEntry
        private boolean containerNestingContainerWithEntry;

    }

}
