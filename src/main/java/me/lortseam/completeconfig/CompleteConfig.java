package me.lortseam.completeconfig;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompleteConfig {

    private static final HashMap<String, ConfigHandler> MANAGERS = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ConfigHandler manager : MANAGERS.values()) {
                manager.save();
            }
        }));
    }

    /**
     * Registers a mod.
     *
     * @param modID      The ID of the mod
     * @return The {@link ConfigHandler} for the newly registered mod
     */
    public static ConfigHandler register(String modID) {
        Objects.requireNonNull(modID);
        if (MANAGERS.containsKey(modID)) {
            throw new IllegalArgumentException("A manager with this mod ID is already registered");
        }
        ConfigHandler manager;
        switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT:
                manager = new ClientConfigHandler(modID);
                break;

            case SERVER:
                manager = new ServerConfigHandler(modID);
                break;

            default:
                throw new IllegalStateException("Illegal environment");
        }
        MANAGERS.put(modID, manager);
        return manager;
    }

    static Optional<ConfigHandler> getManager(String modID) {
        return Optional.ofNullable(MANAGERS.get(modID));
    }

}
