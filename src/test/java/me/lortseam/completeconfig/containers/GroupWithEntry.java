package me.lortseam.completeconfig.containers;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

public class GroupWithEntry implements ConfigGroup {

    @ConfigEntry
    private boolean entry;

}
