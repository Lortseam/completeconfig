package me.lortseam.completeconfig;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompleteConfig {

    private static final HashMap<String, ConfigManager> MANAGERS = new HashMap<>();

    /**
     * Registers a mod
     * @param modID The ID of the mod
     * @return The {@link ConfigManager} for the registered mod
     */
    public static ConfigManager register(String modID) {
        Objects.requireNonNull(modID);
        if (MANAGERS.containsKey(modID)) {
            throw new IllegalArgumentException("A manager with this mod ID is already registered");
        }
        ConfigManager manager = new ConfigManager(modID);
        MANAGERS.put(modID, manager);
        return manager;
    }

    /**
     * Gets the {@link ConfigManager} for the specified mod if that mod was registered before
     * @param modID The ID of the mod
     * @return The {@link ConfigManager} if one was found or else an empty result
     */
    public static Optional<ConfigManager> getManager(String modID) {
        return Optional.ofNullable(MANAGERS.get(modID));
    }

}
