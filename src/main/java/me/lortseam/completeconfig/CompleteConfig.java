package me.lortseam.completeconfig;

import me.lortseam.completeconfig.api.ConfigOwner;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.Objects;

public final class CompleteConfig implements ModInitializer {

    @Override
    public void onInitialize() {
        for (EntrypointContainer<ConfigOwner> entrypoint : FabricLoader.getInstance().getEntrypointContainers("completeconfig", ConfigOwner.class)) {
            ConfigOwner owner = entrypoint.getEntrypoint();
            ConfigBuilder builder = new ConfigBuilder(entrypoint.getProvider().getMetadata().getId(), Objects.requireNonNull(owner.getConfigBranch()), owner.getClass());
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                owner.onInitializeClientConfig(builder);
            }
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
                owner.onInitializeServerConfig(builder);
            }
        }
    }

}
