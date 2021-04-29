package me.lortseam.completeconfig.test.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;

public class ContainerNestingStaticClass implements ConfigContainer {

    @Transitive
    public static class Class {

    }

}
