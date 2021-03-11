package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.groups.GroupWithEntry;

public class ContainerIncludingGroupWithEntry implements ConfigContainer {

    @Override
    public ConfigContainer[] getTransitives() {
        return new ConfigContainer[]{new GroupWithEntry()};
    }

}
