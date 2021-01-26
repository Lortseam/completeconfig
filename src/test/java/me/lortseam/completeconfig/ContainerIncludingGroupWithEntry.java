package me.lortseam.completeconfig;

import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.containers.GroupWithEntry;

public class ContainerIncludingGroupWithEntry implements ConfigEntryContainer {

    @Transitive
    private final GroupWithEntry group = new GroupWithEntry();

}
