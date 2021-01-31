package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.groups.GroupWithEntry;

public class ContainerWithGroupWithEntry implements ConfigEntryContainer {

    @Transitive
    private final GroupWithEntry group = new GroupWithEntry();

}
