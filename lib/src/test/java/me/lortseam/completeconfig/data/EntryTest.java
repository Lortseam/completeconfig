package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.text.TextColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.spongepowered.configurate.CommentedConfigurationNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EntryTest implements ConfigContainer {

    private static final Parent PARENT;
    private static final boolean REQUIRES_RESTART = true;
    private static final String COMMENT = "Comment";
    private static final String CUSTOM_ID = "customId", CUSTOM_TRANSLATION_KEY = "customTranslationKey";

    static {
        ModMetadata modMetadata = mock(ModMetadata.class);
        when(modMetadata.getId()).thenReturn("test");
        Config config = mock(Config.class);
        when(config.getMod()).thenReturn(modMetadata);
        TranslationKey parentTranslation = TranslationKey.from(config).append("subKey");
        PARENT = new Parent() {
            @Override
            public TranslationKey getTranslation() {
                return parentTranslation;
            }
        };
    }

    private static <E extends Entry<?>> void assertEntryType(Entry<?> entry, Class<E> entryType) {
        assertTrue(entryType.isInstance(entry));
    }

    private boolean booleanWithoutAnnotation;
    @ConfigEntry.Boolean
    private boolean booleanWithAnnotation;
    @ConfigEntry.BoundedInteger
    private int boundedInt;
    @ConfigEntry.BoundedInteger
    @ConfigEntry.Slider
    private int sliderInt;
    @ConfigEntry.BoundedLong
    private long boundedLong;
    @ConfigEntry.BoundedLong
    @ConfigEntry.Slider
    private long sliderLong;
    @ConfigEntry.BoundedFloat
    private float boundedFloat;
    @ConfigEntry.BoundedDouble
    private double boundedDouble;
    private AnEnum anEnum = AnEnum.FOO;
    @ConfigEntry.Dropdown
    private AnEnum dropdown = AnEnum.FOO;
    @ConfigEntry.Color(alphaMode = true)
    private int color;
    private TextColor textColor = TextColor.fromRgb(0);

    @ConfigEntry(requiresRestart = REQUIRES_RESTART, comment = COMMENT)
    private int field = 123456789;
    private Entry<?> entry = of("field");
    @ConfigEntry(CUSTOM_ID)
    private boolean customIdField;
    private Entry<?> customIdEntry = of("customIdField");
    @ConfigEntry(translationKey = CUSTOM_TRANSLATION_KEY)
    private boolean customTranslationKeyField;
    private Entry<?> customTranslationKeyEntry = of("customTranslationKeyField");

    private Entry<?> of(String fieldName) {
        try {
            return Entry.of(PARENT, getClass().getDeclaredField((fieldName)), this);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void of_transformTypes() {
        assertEntryType(of("booleanWithoutAnnotation"), BooleanEntry.class);
        assertEntryType(of("booleanWithAnnotation"), BooleanEntry.class);
        assertEntryType(of("boundedInt"), BoundedEntry.class);
        assertEntryType(of("sliderInt"), SliderEntry.class);
        assertEntryType(of("boundedLong"), BoundedEntry.class);
        assertEntryType(of("sliderLong"), SliderEntry.class);
        assertEntryType(of("boundedFloat"), BoundedEntry.class);
        assertEntryType(of("boundedDouble"), BoundedEntry.class);
        assertEntryType(of("anEnum"), EnumEntry.class);
        assertEntryType(of("dropdown"), DropdownEntry.class);
        assertEntryType(of("color"), ColorEntry.class);
    }

    @Test
    @EnabledIfSystemProperty(named = "fabric.dli.env", matches = "client")
    public void of_transformClientTypes() {
        assertEntryType(of("textColor"), ColorEntry.class);
    }

    @Test
    public void of_transformProperties() {
        assertEquals("field", entry.getId());
        assertEquals(CUSTOM_ID, customIdEntry.getId());
        assertEquals(field, entry.getDefaultValue());
        assertTrue(entry.requiresRestart());
        CommentedConfigurationNode node = CommentedConfigurationNode.root();
        entry.fetch(node);
        assertEquals(COMMENT, node.comment());
    }

    @Test
    @EnabledIfSystemProperty(named = "fabric.dli.env", matches = "client")
    public void of_transformClientProperties() {
        assertEquals(PARENT.getTranslation().append(entry.getId()), entry.getTranslation());
        assertEquals(PARENT.getTranslation().append(customIdEntry.getId()), customIdEntry.getTranslation());
        assertEquals(PARENT.getTranslation().root().append(CUSTOM_TRANSLATION_KEY), customTranslationKeyEntry.getTranslation());
        // TODO: Tooltips
    }

    private enum AnEnum {
        FOO
    }

}
