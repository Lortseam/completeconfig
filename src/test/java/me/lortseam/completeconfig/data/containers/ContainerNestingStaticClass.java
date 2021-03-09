package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;

public class ContainerNestingStaticClass implements ConfigContainer {

    @Transitive
    public static class Class {

    }

}
