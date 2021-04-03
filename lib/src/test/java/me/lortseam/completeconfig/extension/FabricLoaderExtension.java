package me.lortseam.completeconfig.extension;

import com.google.common.jimfs.Jimfs;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.mockito.Mockito.*;

public class FabricLoaderExtension implements BeforeAllCallback {

    private static boolean initialized = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (initialized) return;
        init();
        initialized = true;
    }

    private void init() {
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
