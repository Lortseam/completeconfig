package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.test.data.groups.CustomGroup;
import me.lortseam.completeconfig.test.data.groups.EmptyGroup;
import me.lortseam.completeconfig.text.TranslationBase;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.resource.language.I18n;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ClusterTest {

    private static final String MOD_ID = "test",
            CUSTOM_ID = "testId",
            CUSTOM_NAME_KEY = "testNameKey",
            CUSTOM_DESCRIPTION_KEY = "testDescriptionKey";

    private Cluster defaultCluster;
    private Cluster customCluster;

    @BeforeEach
    public void beforeEach() {
        var config = mock(Config.class);
        ModMetadata modMetadata = mock(ModMetadata.class);
        when(modMetadata.getId()).thenReturn(MOD_ID);
        when(config.getMod()).thenReturn(modMetadata);
        when(config.getRoot()).thenCallRealMethod();
        var rootTranslation = new TranslationKey(config);
        when(config.getBaseTranslation()).thenReturn(rootTranslation);
        defaultCluster = new Cluster(config, new EmptyGroup());
        customCluster = new Cluster(config, new CustomGroup(CUSTOM_ID, CUSTOM_NAME_KEY, CUSTOM_DESCRIPTION_KEY));
    }

    @Test
    public void getId_test() {
        assertEquals("emptyGroup", defaultCluster.getId());
        assertEquals(CUSTOM_ID, customCluster.getId());
    }

    @Test
    public void getBaseTranslation_test() {
        assertEquals("config." + MOD_ID + "." + CUSTOM_ID, customCluster.getBaseTranslation(TranslationBase.INSTANCE, null).toString());
        assertEquals("config." + MOD_ID + ".customGroup", customCluster.getBaseTranslation(TranslationBase.CLASS, CustomGroup.class).toString());
        assertEquals("config." + MOD_ID + ".customGroup", customCluster.getBaseTranslation(TranslationBase.CLASS, null).toString());
    }

    @Test
    public void getNameTranslation_test() {
        assertEquals("config." + MOD_ID + ".emptyGroup", defaultCluster.getNameTranslation().toString());
        assertEquals("config." + MOD_ID + "." + CUSTOM_NAME_KEY, customCluster.getNameTranslation().toString());
    }

    @Test
    public void getDescriptionTranslation_test() {
        try (var i18n = mockStatic(I18n.class)) {
            var defaultKey = "config." + MOD_ID + ".emptyGroup.description";
            i18n.when(() -> I18n.hasTranslation(defaultKey)).thenReturn(true);
            assertEquals(defaultKey, defaultCluster.getDescriptionTranslation().get().toString());

            var customKey = "config." + MOD_ID + "." + CUSTOM_DESCRIPTION_KEY;
            i18n.when(() -> I18n.hasTranslation(customKey)).thenReturn(true);
            assertEquals(customKey, customCluster.getDescriptionTranslation().get().toString());
        }
    }

}
