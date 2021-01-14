package me.lortseam.completeconfig;

import lombok.Getter;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.util.TypeUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.text.TextColor;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ModController {

    private static final Map<String, ModController> controllers = new HashMap<>();
    private static final TypeSerializerCollection GLOBAL_TYPE_SERIALIZERS = TypeSerializerCollection.builder()
            .registerExact(TypeSerializer.of(TextColor.class, (item, typeSupported) -> {
                return item.getRgb();
            }, value -> {
                if (value instanceof Integer) {
                    return TextColor.fromRgb((int) value);
                }
                throw new CoercionFailedException(value, "TextColor");
            }))
            .build();

    /**
     * Gets the mod controller of a loaded mod.
     *
     * @param id the ID of the mod
     * @return the corresponding mod controller
     */
    public static ModController of(String id) {
        ModMetadata metadata = FabricLoader.getInstance().getModContainer(id).map(ModContainer::getMetadata).orElseThrow(() -> {
            return new IllegalArgumentException("Mod " + id + " is not loaded");
        });
        if (!controllers.containsKey(metadata.getId())) {
            ModController controller = new ModController(metadata);
            controllers.put(metadata.getId(), controller);
            return controller;
        }
        return controllers.get(metadata.getId());
    }

    @Getter
    private final ModMetadata metadata;
    @Getter
    private TypeSerializerCollection typeSerializers;

    private ModController(ModMetadata metadata) {
        this.metadata = metadata;
        registerTypeSerializers(GLOBAL_TYPE_SERIALIZERS);
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
     * <p>To register type serializers for a specific config only, use
     * {@link Config.Builder#registerTypeSerializers(TypeSerializerCollection)}.
     *
     * @param typeSerializers the type serializers
     */
    public void registerTypeSerializers(TypeSerializerCollection typeSerializers) {
        this.typeSerializers = TypeUtils.mergeSerializers(this.typeSerializers, Objects.requireNonNull(typeSerializers));
    }

}
