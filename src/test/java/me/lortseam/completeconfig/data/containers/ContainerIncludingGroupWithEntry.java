package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.data.groups.GroupWithEntry;

public class ContainerIncludingGroupWithEntry implements ConfigEntryContainer {

    @Transitive
    private final GroupWithEntry group = new GroupWithEntry();

}
