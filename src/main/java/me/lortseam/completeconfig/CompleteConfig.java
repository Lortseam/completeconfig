package me.lortseam.completeconfig;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompleteConfig {

    private static final HashMap<String, ConfigManager> MANAGERS = new HashMap<>();

    public static ConfigManager register(String modID) {
        if (MANAGERS.containsKey(modID)) {
            throw new IllegalArgumentException("A manager with this mod ID is already registered");
        }
        ConfigManager manager = new ConfigManager(modID);
        MANAGERS.put(modID, manager);
        return manager;
    }

    public static Optional<ConfigManager> getManager(String modID) {
        return Optional.ofNullable(MANAGERS.get(modID));
    }

}
