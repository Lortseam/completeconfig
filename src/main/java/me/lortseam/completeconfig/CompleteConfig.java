package me.lortseam.completeconfig;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompleteConfig {

    private static final Set<ConfigManager> managers = new HashSet<>();

    public static ConfigManager register(String modID) {
        if (getManager(modID).isPresent()) {
            throw new IllegalArgumentException("A manager with this mod ID is already registered");
        }
        ConfigManager manager = new ConfigManager(modID);
        managers.add(manager);
        return manager;
    }

    public static Optional<ConfigManager> getManager(String modID) {
        return managers.stream().filter(manager -> manager.getModID().equals(modID)).findAny();
    }

}
