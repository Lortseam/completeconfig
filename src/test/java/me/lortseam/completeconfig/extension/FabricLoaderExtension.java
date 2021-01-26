package me.lortseam.completeconfig.extension;

import com.google.common.jimfs.Jimfs;
import net.fabricmc.loader.api.FabricLoader;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.mockito.Mockito.*;

public class FabricLoaderExtension implements BeforeAllCallback {

    private static boolean mocked = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!mocked) {
            FabricLoader loader = mock(FabricLoader.class);
            when(loader.isModLoaded(any(String.class))).thenReturn(true);
            when(loader.getConfigDir()).thenReturn(Jimfs.newFileSystem().getPath(""));
            mockStatic(FabricLoader.class).when(FabricLoader::getInstance).thenReturn(loader);
            mocked = true;
        }
    }

}
