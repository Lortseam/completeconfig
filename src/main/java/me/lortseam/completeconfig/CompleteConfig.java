package me.lortseam.completeconfig;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CompleteConfig {

    private static final Set<ConfigManager> managers = new HashSet<>();

    public static ConfigManager register(String modID) {
        if (getManager(modID).isPresent()) {
            throw new RuntimeException("There is already registered a manager for this mod ID!");
        }
        ConfigManager manager = new ConfigManager(modID);
        managers.add(manager);
        return manager;
    }

    public static Optional<ConfigManager> getManager(String modID) {
        return managers.stream().filter(manager -> manager.getModID().equals(modID)).findAny();
    }

}
