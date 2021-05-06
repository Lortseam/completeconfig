package me.lortseam.completeconfig.test.extension;

import com.google.common.jimfs.Jimfs;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.junit.jupiter.api.extension.Extension;

import static org.mockito.Mockito.*;

public class FabricLoaderExtension implements Extension {

    static {
        FabricLoader loader = mock(FabricLoader.class);
        switch (System.clearProperty("fabric.dli.env")) {
            case "client":
                when(loader.getEnvironmentType()).thenReturn(EnvType.CLIENT);
                break;

            case "server":
                when(loader.getEnvironmentType()).thenReturn(EnvType.SERVER);
                break;

            default:
                throw new IllegalArgumentException("Unknown environment property");
        }
        when(loader.isModLoaded(any(String.class))).thenReturn(true);
        when(loader.getConfigDir()).thenReturn(Jimfs.newFileSystem().getPath(""));
        mockStatic(FabricLoader.class).when(FabricLoader::getInstance).thenReturn(loader);
    }

}
