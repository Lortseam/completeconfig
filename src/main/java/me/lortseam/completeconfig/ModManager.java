package me.lortseam.completeconfig;

import lombok.Getter;
import me.lortseam.completeconfig.util.TypeUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ModManager {

    private static final Map<String, ModManager> controllers = new HashMap<>();

    /**
     * Gets the mod controller of a loaded mod.
     *
     * @param id the ID of the mod
     * @return the corresponding mod controller
     */
    public static ModManager of(String id) {
        ModMetadata metadata = FabricLoader.getInstance().getModContainer(id).map(ModContainer::getMetadata).orElseThrow(() -> {
            return new IllegalArgumentException("Mod " + id + " is not loaded");
        });
        if (!controllers.containsKey(metadata.getId())) {
            ModManager controller = new ModManager(metadata);
            controllers.put(metadata.getId(), controller);
            return controller;
        }
        return controllers.get(metadata.getId());
    }

    @Getter
    private final ModMetadata metadata;
    @Getter
    private TypeSerializerCollection typeSerializers;

    private ModManager(ModMetadata metadata) {
        this.metadata = metadata;
    }

    public String getID() {
        return metadata.getId();
    }

    public String getName() {
        return metadata.getName();
    }

    /**
     * Registers custom type serializers, applied to all following mod configs.
     *
     * @param typeSerializers the type serializers
     */
    public void registerTypeSerializers(TypeSerializerCollection typeSerializers) {
        this.typeSerializers = TypeUtils.mergeSerializers(this.typeSerializers, Objects.requireNonNull(typeSerializers));
    }

}
