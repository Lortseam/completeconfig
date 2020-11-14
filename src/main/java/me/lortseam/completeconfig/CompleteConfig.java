package me.lortseam.completeconfig;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompleteConfig {

    private static final HashMap<String, ConfigManager> MANAGERS = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ConfigManager manager : MANAGERS.values()) {
                manager.save();
            }
        }));
    }

    /**
     * Registers a mod.
     *
     * @param modID      The ID of the mod
     * @return The {@link ConfigManager} for the newly registered mod
     */
    public static ConfigManager register(String modID) {
        Objects.requireNonNull(modID);
        if (MANAGERS.containsKey(modID)) {
            throw new IllegalArgumentException("A manager with this mod ID is already registered");
        }
        ConfigManager manager;
        switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT:
                manager = new ClientConfigManager(modID);
                break;

            case SERVER:
                manager = new ServerConfigManager(modID);
                break;

            default:
                throw new IllegalStateException("Illegal environment");
        }
        MANAGERS.put(modID, manager);
        return manager;
    }

    static Optional<ConfigManager> getManager(String modID) {
        return Optional.ofNullable(MANAGERS.get(modID));
    }

}
