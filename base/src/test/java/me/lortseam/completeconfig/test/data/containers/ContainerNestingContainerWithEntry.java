package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigContainer;

public class ContainerNestingContainerWithEntry implements ConfigContainer {

    @Transitive
    public class Container implements ConfigContainer {

        @ConfigEntry
        private boolean cncweEntry;

    }

}
