package me.lortseam.completeconfig.data;

import lombok.*;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.data.extension.DataExtension;
import me.lortseam.completeconfig.data.transform.Transformation;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class ConfigOptions {

    /**
     * Creates a {@link ConfigOptions.Builder} for the specified mod.
     *
     * @param modId the ID of the mod creating the config
     */
    public static ConfigOptions.Builder mod(@NonNull String modId) {
        if (!FabricLoader.getInstance().isModLoaded(modId)) {
            throw new IllegalArgumentException("Mod " + modId + " is not loaded");
        }
        return new Builder(modId);
    }

    @EqualsAndHashCode.Include
    @ToString.Include
    @Getter(AccessLevel.PACKAGE)
    private final String modId;
    @EqualsAndHashCode.Include
    @ToString.Include
    @Getter(AccessLevel.PACKAGE)
    private final String[] branch;
    private final TypeSerializerCollection typeSerializers;
    private final String fileHeader;
    @Getter(AccessLevel.PACKAGE)
    private final ConfigRegistry registry = new ConfigRegistry();

    private ConfigOptions(String modId, String[] branch, TypeSerializerCollection typeSerializers, List<Transformation> transformations, String fileHeader) {
        this.modId = modId;
        this.branch = branch;
        this.typeSerializers = typeSerializers;
        registry.registerTransformations(transformations);
        this.fileHeader = fileHeader;
    }

    HoconConfigurationLoader createDefaultLoader() {
        return createLoader(builder -> {
            Path path = FabricLoader.getInstance().getConfigDir();
            String[] subPath = ArrayUtils.addFirst(branch, modId);
            subPath[subPath.length - 1] = subPath[subPath.length - 1] + ".conf";
            for (String child : subPath) {
                path = path.resolve(child);
            }
            builder.path(path);
        });
    }

    HoconConfigurationLoader createLoader(Consumer<HoconConfigurationLoader.Builder> builderConsumer) {
        HoconConfigurationLoader.Builder builder = HoconConfigurationLoader.builder()
                .defaultOptions(options -> options
                        .serializers(typeSerializersBuilder -> {
                            typeSerializersBuilder.registerAll(typeSerializers);
                            for (TypeSerializerCollection typeSerializers : CompleteConfig.collectExtensions(DataExtension.class, DataExtension::getTypeSerializers)) {
                                typeSerializersBuilder.registerAll(typeSerializers);
                            }
                        })
                        .header(fileHeader)
                );
        builderConsumer.accept(builder);
        return builder.build();
    }

    public static final class Builder {

        private final String modId;
        private String[] branch = new String[0];
        private String fileHeader;
        private final TypeSerializerCollection.Builder typeSerializerCollectionBuilder = TypeSerializerCollection.builder();
        private final List<Transformation> transformations = new ArrayList<>();

        private Builder(String modId) {
            this.modId = modId;
        }

        /**
         * Sets the config branch. The branch determines the location of the config file and has to be mod-unique.
         *
         * @param branch the branch
         * @return this builder
         */
        public Builder branch(@NonNull String[] branch) {
            Arrays.stream(branch).forEach(Objects::requireNonNull);
            this.branch = branch;
            return this;
        }

        public Builder typeSerializers(@NonNull TypeSerializerCollection typeSerializers) {
            typeSerializerCollectionBuilder.registerAll(typeSerializers);
            return this;
        }

        public Builder objectMapperFactory(@NonNull ObjectMapper.Factory objectMapperFactory) {
            typeSerializerCollectionBuilder.registerAnnotatedObjects(objectMapperFactory);
            return this;
        }

        public Builder transformation(@NonNull Transformation transformation) {
            transformations.add(transformation);
            return this;
        }

        public Builder transformations(@NotNull List<Transformation> transformations) {
            this.transformations.addAll(transformations);
            return this;
        }

        public Builder fileHeader(@NonNull String fileHeader) {
            this.fileHeader = fileHeader;
            return this;
        }

        ConfigOptions build() {
            return new ConfigOptions(modId, branch.clone(), typeSerializerCollectionBuilder.build(), transformations, fileHeader);
        }

    }

}
