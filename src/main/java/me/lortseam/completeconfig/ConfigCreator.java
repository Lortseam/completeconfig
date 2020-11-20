package me.lortseam.completeconfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigCreator {

    @Environment(EnvType.CLIENT)
    public ClientConfigHandler clientConfig() {

    }

    @Environment(EnvType.SERVER)
    public ServerConfigHandler serverConfig() {

    }

    public ConfigHandler config() {
        switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT:
                return clientConfig();

            case SERVER:
                return serverConfig();

            default:
                //TODO
        }
    }

}
