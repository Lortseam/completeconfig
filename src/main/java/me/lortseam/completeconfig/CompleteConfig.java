package me.lortseam.completeconfig;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.gui.GuiBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothGuiBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompleteConfig {

    private static final HashMap<String, ConfigManager> MANAGERS = new HashMap<>();

    /**
     * Registers a mod with a custom GUI builder.
     *
     * @param modID      The ID of the mod
     * @param guiBuilder The {@link GuiBuilder} for the mod's config GUI
     * @return The {@link ConfigManager} for the newly registered mod
     */
    public static ConfigManager register(String modID, GuiBuilder guiBuilder) {
        Objects.requireNonNull(modID);
        if (MANAGERS.containsKey(modID)) {
            throw new IllegalArgumentException("A manager with this mod ID is already registered");
        }
        ConfigManager manager = new ConfigManager(modID, guiBuilder);
        MANAGERS.put(modID, manager);
        return manager;
    }

    /**
     * Registers a mod. Uses the default Cloth Config GUI builder in a client environment if Cloth Config is installed.
     *
     * @param modID The ID of the mod
     * @return The {@link ConfigManager} for the newly registered mod
     */
    public static ConfigManager register(String modID) {
        return register(modID, FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                && FabricLoader.getInstance().isModLoaded("cloth-config2") ? new ClothGuiBuilder() : null);
    }

    static Optional<ConfigManager> getManager(String modID) {
        return Optional.ofNullable(MANAGERS.get(modID));
    }

}
